package org.nix.sharedlib.agent

import org.nix.sharedlib.agent.kubernetes.KubeBuildAgent
import org.nix.sharedlib.agent.kubernetes.KubeBuildDotnetAgent
import org.nix.sharedlib.agent.kubernetes.KubeBuildNodejsAgent

/**
 * Build agent factory
 */
class BuildAgentFactory {

    /**
     * get build agent
     */
    static AgentRunner getBuildAgent(Script script) {
        return new KubeBuildAgent(script)
    }

    /**
     * get build .Net agent
     */
    static AgentRunner getBuildDotnetAgent(Script script) {
        return new KubeBuildDotnetAgent(script)
    }

    /**
     * get build Node.js agent
     */
    static AgentRunner getBuildNodejsAgent(Script script) {
        return new KubeBuildNodejsAgent(script)
    }

}
