package org.apache.bigtop.manager.common.pojo.stack;

import lombok.Data;
import org.apache.bigtop.manager.common.enums.ArchType;
import org.apache.bigtop.manager.common.enums.OSType;

import java.util.List;

@Data
public class OSSpecific {

    private List<String> os;

    private List<String> arch;

    private List<String> packages;
}
