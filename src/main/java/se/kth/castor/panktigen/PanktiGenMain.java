package se.kth.castor.panktigen;

import picocli.CommandLine;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.SpoonPom;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "java -jar target/<pankti-gen-version-jar-with-dependencies.jar>",
        description = "pankti-gen generates test cases from serialized objects",
        usageHelpWidth = 100)
public class PanktiGenMain implements Callable<Integer> {
    @CommandLine.Parameters(
            paramLabel = "PATH",
            description = "Path of the Maven project")
    private Path projectPath;

    @CommandLine.Option(
            names = {"-h", "--help"},
            description = "Display help/usage.",
            usageHelp = true)
    private boolean usageHelpRequested;

    public PanktiGenMain() {}

    public Path getProjectPath() {
        return projectPath;
    }

    public PanktiGenMain(final Path projectPath, final boolean help) {
        this.projectPath = projectPath;
        this.usageHelpRequested = help;
    }

    public Integer call() {
        if (usageHelpRequested) {
            return 1;
        }
        final String path = this.projectPath.toString();
        final String name = this.projectPath.getFileName().toString();
        PanktiGenLauncher panktiGenLauncher = new PanktiGenLauncher();
        MavenLauncher launcher = panktiGenLauncher.getMavenLauncher(path, name);
        SpoonPom projectPom = launcher.getPomFile();

        CtModel model = panktiGenLauncher.buildSpoonModel(launcher);
        TestGenerator testGenerator = new TestGenerator();
        List<CtType<?>> classTypes = testGenerator.getNonAbstractClassTypes(model);
        System.out.println("Class types: " + classTypes.size());

        System.out.println("POM found at: " + projectPom.getPath());
        System.out.println("Number of Maven modules: " + projectPom.getModel().getModules().size());

        // Save model in outputdir/

        String outputDirectory = "/home/user/generated/" + name;
        launcher.setSourceOutputDirectory(outputDirectory);
        launcher.prettyprint();
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PanktiGenMain()).execute(args);
        System.exit(exitCode);
    }
}