package org.apache.bigtop.manager.stack.core.hooks;


import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;
import org.apache.bigtop.manager.common.utils.os.OSDetection;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.common.utils.PackageUtils;
import org.apache.bigtop.manager.stack.common.utils.template.BaseTemplate;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;

import java.util.List;
import java.util.Set;

/**
 * obtain agent execute command
 */
@Slf4j
@AutoService(Hook.class)
public class InstallHookImpl implements Hook {

    @Override
    @HookAnnotation(before = HookType.ANY)
    public void before() {
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
    @HookAnnotation(after = HookType.ANY)
    public void after() {
    }

    @Override
    public String getName() {
        return HookType.INSTALL.name();
    }
}
