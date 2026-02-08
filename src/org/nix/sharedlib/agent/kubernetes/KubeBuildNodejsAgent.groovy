package org.nix.sharedlib.agent.kubernetes

import org.csanchez.jenkins.plugins.kubernetes.ContainerTemplate

/**
 * Kubernetes build Node.js agent
 */
class KubeBuildNodejsAgent extends KubeAgent {

    protected final static String BUILD_NODEJS_AGENT_IMAGE_VERSION = '22'

    KubeBuildNodejsAgent(Script script) {
        super(script)
    }

    @Override
    void nodeWrapper(int timeout, Map args = [:], Closure body) {
        boolean useBuildkit = args.get('useBuildkit', false)
        script.podTemplate(
            cloud: CLOUD_NAME,
            yaml: getBuildContainerSecuritySpec(useBuildkit),
            containers: [
                getBuildNodejsContainerSpec(args, useBuildkit),
                jnlpContainerSpec,
            ]
        ) {
            script.node(script.env.POD_LABEL) {
                script.timeout(time: timeout) {
                    script.ansiColor('xterm') {
                        containerWrapper(BUILD_CONTAINER_NAME) {
                            body.call()
                        }
                    }
                }
            }
        }
    }

    /**
     * get build Node.js container spec
     */
    ContainerTemplate getBuildNodejsContainerSpec(Map args = [:], boolean useBuildkit = false) {
        String nodejsVersion = args.get('nodejsVersion', BUILD_NODEJS_AGENT_IMAGE_VERSION)
        String image = getBuildNodejsAgentImage(nodejsVersion)
        return script.containerTemplate(
            args: getBuildkitArgs(useBuildkit),
            alwaysPullImage: false,
            command: getBuildkitEntrypoint(useBuildkit),
            envVars: [],
            image: image,
            name: BUILD_CONTAINER_NAME,
            resourceRequestCpu: args.get(CONTAINER_CPU_REQUEST_ARG_NAME, BUILD_CONTAINER_CPU_REQUEST),
            resourceLimitCpu: args.get(CONTAINER_CPU_LIMIT_ARG_NAME, BUILD_CONTAINER_CPU_LIMIT),
            resourceRequestMemory: args.get(CONTAINER_MEMORY_REQUEST_ARG_NAME, BUILD_CONTAINER_MEMORY_REQUEST),
            resourceLimitMemory: args.get(CONTAINER_MEMORY_LIMIT_ARG_NAME, BUILD_CONTAINER_MEMORY_LIMIT),
            runAsUser: DEFAULT_CONTAINER_USER,
            runAsGroup: DEFAULT_CONTAINER_GROUP,
            ttyEnabled: true,
        )
    }

    /**
     * get Node.js agent image
     */
    String getBuildNodejsAgentImage(String nodejsVersion) {
        switch (nodejsVersion) {
            case '22':
                return 'nix-docker.registry.twcstorage.ru/ci/build/nodejs-build:22.20.0000-snapshot' +
                    '@sha256:1269085a8a4f45ed1b1f305671612d232fbb1072ad2fae26561fda58b5ed23ee'
            default:
                throw new IllegalArgumentException("Unsupported Node.js version: ${nodejsVersion}")
        }
    }

}
