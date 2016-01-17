package com.comsysto.gradle

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class CroLaBeFraMothershipPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.tasks.create(
                [
                        name: 'clean',
                        type: Delete
                ],
                {
                    delete project.buildDir
                }
        )

        def crolabefra = project.tasks.create(
                [
                        name: 'crolabefra',
                        group: 'crolabefra',
                        dependsOn: {
                            def result = [];
                            project.allprojects {
                                if(it.plugins.findPlugin('com.comsysto.gradle.crolabefra.cpp')) {
                                    println("Found C++ Drone Plugin ...")
                                    result.add(":${it.name}:runCppBenchmarks")
                                }
                                if(it.plugins.findPlugin('com.comsysto.gradle.crolabefra.java')) {
                                    println("Found Java Drone Plugin ...")
                                    result.add(":${it.name}:runJavaBenchmarks")
                                }
                            }

                            return result;
                        },
                        type: Copy
                ],
                {
                    def toDir = new File(project.buildDir, "/results")
                    outputs.upToDateWhen { false }
                    from({
                        project.zipTree(project.buildscript.configurations.classpath.find { jars ->
                            jars.name.contains 'crolabefra-mothership'
                        }
                        ).getAsFileTree().matching {
                            include 'mothership/**'
                        }
                    })
                    into toDir
                }
        )

        crolabefra.doFirst {
            def FileTree jsonFiles = project.fileTree(new File(project.buildDir, "/results/mothership/data")).include('*.js')
            def dataScripts = '';
            jsonFiles.each { file ->
                println file.name
                dataScripts += "<script src='./data/${file.name}' type='text/javascript'></script>\n"
            }
            //TODO: Hm quite heavy pattern matching ... affects all files. Probably split index.html transfer out.
            filter(ReplaceTokens, tokens: [crolabefraData: dataScripts])
        }

    }
}
