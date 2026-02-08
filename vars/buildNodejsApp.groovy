import org.nix.sharedlib.pipeline.build.BuildNodejsAppPipeline

/**
 * Build Node.js app pipeline
 */
void call(Map args = [:]) {
    new BuildNodejsAppPipeline(this).run(args)
}
