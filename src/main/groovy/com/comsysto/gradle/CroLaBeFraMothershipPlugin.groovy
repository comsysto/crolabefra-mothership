package com.comsysto.gradle;

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Copy

class CroLaBeFraMothershipPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.tasks.create(
                [
                        name     : 'crolabefra',
                        dependsOn: 'crolabefra',
                        type     : Copy
                ],
                {
                    outputs.dir "$buildDir/results"
                    from project.resources.text.fromArchiveEntry(
                            project.buildscript.configurations.classpath.findAll {
                                it.name.contains 'crolabefra-mothership'
                            },
                            'mothership'
                    ).asFile()
                    into "$buildDir/results"

                    doFirst {
                        def FileTree jsonFiles = fileTree("$buildDir/results/data").include('*.js')
                        def dataScripts = '';
                        jsonFiles.each { file ->
                            println file.name
                            dataScripts += "<script src='./data/${file.name}' type='text/javascript'></script>\n"
                        }
                        //TODO: Hm quite heavy pattern matching ... affects all files. Probably split index.html transfer out.
                        filter(ReplaceTokens, tokens: [crolabefraData: dataScripts])
                    }
                }
        )


        task runBenchmarks(dependsOn: []) {
        }

        task mapResults(dependsOn: []) {
        }


    }
}
