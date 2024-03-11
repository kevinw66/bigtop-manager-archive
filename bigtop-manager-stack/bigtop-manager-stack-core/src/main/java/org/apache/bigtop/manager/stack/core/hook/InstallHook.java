package org.apache.bigtop.manager.stack.core.hook;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.entity.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.bigtop.manager.spi.stack.Hook;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.template.BaseTemplate;

import java.util.List;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class InstallHook extends AbstractHook {

    public static final String NAME = "install";

    @Override
    public void doBefore() {
        List<RepoInfo> repos = LocalSettings.repos();
        String repoTemplate = LocalSettings.cluster().getRepoTemplate();

        for (RepoInfo repo : repos) {
            if (OSDetection.getOS().equals(repo.getOs()) && OSDetection.getArch().equals(repo.getArch())) {
                BaseTemplate.writeCustomTemplate("/etc/yum.repos.d/" + repo.getRepoId().replace(".", "_") + ".repo", repo, repoTemplate);
            }
        }

        List<String> packages = LocalSettings.packages();
        PackageUtils.install(packages);
    }

    @Override
    public void doAfter() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
