package zzzank.probejs.features.forge_scan;

import lombok.val;
import net.minecraftforge.fml.ModList;
import zzzank.probejs.ProbeConfig;
import zzzank.probejs.utils.ReflectUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
public class ForgeModScanner {

    public List<Class<?>> scanAll() {
        val allScanData = ModList.get().getAllScanData();
        val scanner = ProbeConfig.fullScan.get()
            ? ClassDataScanner.FULL_SCAN
            : ClassDataScanner.SUBCLASSES;
        return scanner.scan(
                allScanData
                    .stream()
                    .flatMap(data -> data.getClasses().stream())
            )
            .stream()
            .map(ReflectUtils::classOrNull)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
