package zzzank.probejs.features.kubejs;

import dev.latvian.mods.rhino.CompilerEnvirons;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Parser;
import dev.latvian.mods.rhino.ast.*;
import lombok.val;
import zzzank.probejs.utils.GameUtils;
import zzzank.probejs.utils.NameUtils;
import zzzank.probejs.ProbeConfig;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ScriptTransformer {
    private static final String PLACEHOLDER = "!@#$%^"; // placeholder to not mutate original string length

    private static final Supplier<Parser> PARSER = () -> {
        val compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.initFromContext(Context.enterWithNewFactory());
        Context.exit();
        return new Parser(compilerEnvirons);
    };

    public final Set<String> exportedSymbols;
    private int requireCounts;
    public List<String> lines;

    public ScriptTransformer(String[] lines) {
        this.lines = Arrays.asList(lines);
        requireCounts = 0;
        this.exportedSymbols = new HashSet<>();
    }

    // process the const require
    public void processRequire() {
        String joined = String.join("\n", lines);
        val root = PARSER.get().parse(joined, "probejs_parse.js", 0);
        List<Integer[]> cuts = new ArrayList<>();

        for (val statement : root.getStatements()) {
            // declaring
            if (!(statement instanceof VariableDeclaration declaration)) {
                continue;
            }
            val variables = declaration.getVariables();
            for (val variable : variables) {
                // used require()
                if (
                    !(variable.getInitializer() instanceof FunctionCall call)
                        || !(call.getTarget() instanceof Name name)
                        || !name.getIdentifier().equals("require")
                ) {
                    continue;
                }
                val loaded = call.getArguments().get(0);
                if (!(loaded instanceof StringLiteral literal)) {
                    //well, that should not happen, `require()` is no supposed to be used for handling non-string object
                    continue;
                }
                requireCounts++;
                // is java package, transform if it's const
                if (literal.getValue().startsWith("packages/")) {
                    if (declaration.isConst()) {
                        joined = NameUtils.replaceRegion(
                            joined,
                            statement.getPosition(),
                            statement.getPosition() + statement.getLength(),
                            "const ",
                            PLACEHOLDER
                        );
                    }
                } else {
                    // not java package, cut it
                    cuts.add(new Integer[]{
                        statement.getPosition(), statement.getPosition() + statement.getLength()
                    });
                }
            }
        }

        cuts.sort(Comparator.comparing(p -> p[0]));
        joined = NameUtils.cutOffStartEnds(joined, cuts);

        joined = joined.replace(PLACEHOLDER, "let ");
        lines = Arrays.asList(joined.split("\\n"));
    }

    // scans for the export function/let/var/const
    public void processExport() {
        for (int i = 0; i < lines.size(); i++) {
            String tLine = lines.get(i).trim();
            if (!tLine.startsWith("export ")) {
                continue;
            }
            tLine = tLine.substring(7).trim();
            val parts = tLine.split(" ", 2);

            val identifier = switch (parts[0]) {
                case "function" -> parts[1].split("\\(")[0];
                case "var", "let", "const" -> parts[1].split(" ")[0];
                default -> null;
            };
            if (identifier == null) {
                continue;
            }

            exportedSymbols.add(identifier);
            lines.set(i, tLine);
        }
    }

    // Wraps the code in let {...} = (()=>{...;return {...};})()
    public void wrapScope() {
        val exported = exportedSymbols
            .stream()
            .map(s -> String.format("%s: %s", s, s))
            .collect(Collectors.joining(", "));
        lines.set(0, String.format("const {%s} = (()=>{ %s", exported, lines.get(0)));
        lines.set(lines.size() - 1, String.format("%s; return {%s};})()", lines.get(lines.size() - 1), exported));
    }

    public String[] transform() {
        try {
            processExport();
            processRequire();
            // If there's no symbol to be exported, and no `require()` call, it will not be marked as CommonJS module
            if (ProbeConfig.isolatedScopes.get() && (!exportedSymbols.isEmpty() || requireCounts != 0)) {
                wrapScope();
            }
        } catch (Throwable t) {
            GameUtils.logThrowable(t);
        }

        return lines.toArray(new String[0]);
    }
}