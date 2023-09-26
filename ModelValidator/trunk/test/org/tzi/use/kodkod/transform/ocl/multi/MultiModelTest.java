package org.tzi.use.kodkod.transform.ocl.multi;

import junit.framework.TestCase;
import org.junit.Test;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.ocl.OCLGroupRegistry;
import org.tzi.kodkod.ocl.operation.*;
import org.tzi.use.config.Options;
import org.tzi.use.kodkod.plugin.KodkodValidateConfigurationAction;
import org.tzi.use.kodkod.plugin.PluginModelFactory;
import org.tzi.use.main.Session;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.parser.use.USECompilerMulti;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.MultiModelFactory;
import org.tzi.use.uml.sys.MSystem;

import java.io.*;

public class MultiModelTest extends TestCase{

    @Test
    public void test1() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/const_fail.use");
        MModel mModel;
        try (FileInputStream specStream = new FileInputStream(file)){
            mModel = USECompiler.compileSpecification(specStream, "const_fail.use", new PrintWriter(System.err), new MultiModelFactory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/const.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be unsatisfiable");
    }

    @Test
    public void test2() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/const_success.use");
        MModel mModel;
        try (FileInputStream specStream = new FileInputStream(file)){
            mModel = USECompiler.compileSpecification(specStream, "const_success.use", new PrintWriter(System.err), new MultiModelFactory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/const.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be satisfiable");
    }

    @Test
    public void test3() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi_const_fail.use");
        MModel mModel;
        MMultiModel multiModel;
        try (FileInputStream specStream = new FileInputStream(file)){
            multiModel = USECompilerMulti.compileMultiSpecification(specStream, "multi_const_fail.use", new PrintWriter(System.err), new MultiModelFactory());
            mModel = multiModel.toMModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi_const.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be unsatisfiable");
    }

    @Test
    public void test4() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi_const_success.use");
        MModel mModel;
        MMultiModel multiModel;
        try (FileInputStream specStream = new FileInputStream(file)){
            multiModel = USECompilerMulti.compileMultiSpecification(specStream, "multi_const_success.use", new PrintWriter(System.err), new MultiModelFactory());
            mModel = multiModel.toMModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi_const.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be satisfiable");
    }


    @Test
    public void test5() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi1.use");
        MModel mModel;
        MMultiModel multiModel;
        try (FileInputStream specStream = new FileInputStream(file)){
            multiModel = USECompilerMulti.compileMultiSpecification(specStream, "multi1.use", new PrintWriter(System.err), new MultiModelFactory());
            mModel = multiModel.toMModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi1.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be satisfiable");
    }

    @Test
    public void test6() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi2.use");
        MModel mModel;
        MMultiModel multiModel;
        try (FileInputStream specStream = new FileInputStream(file)) {
            multiModel = USECompilerMulti.compileMultiSpecification(specStream, "multi2.use", new PrintWriter(System.err), new MultiModelFactory());
            mModel = multiModel.toMModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi2.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be unsatisfiable");
    }

    @Test
    public void test7() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi3.use");
        MModel mModel;
        MMultiModel multiModel;
        try (FileInputStream specStream = new FileInputStream(file)){
            multiModel = USECompilerMulti.compileMultiSpecification(specStream, "multi3.use", new PrintWriter(System.err), new MultiModelFactory());
            mModel = multiModel.toMModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/multi3.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be unsatisfiable");
    }

    @Test
    public void test8() throws FileNotFoundException {
        File file = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/mmContradictory.use");
        MModel mModel;
        MMultiModel multiModel;
        try (FileInputStream specStream = new FileInputStream(file)){
            multiModel = USECompilerMulti.compileMultiSpecification(specStream, "mmContradictory.use", new PrintWriter(System.err), new MultiModelFactory());
            mModel = multiModel.toMModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Session session = new Session();
        MSystem mSystem = new MSystem(mModel);
        session.setSystem(mSystem);
        PrintWriter errorWriter = new PrintWriter(System.out);

        IModel model = PluginModelFactory.INSTANCE.getModel(mModel);
        File f = new File("trunk/test/org/tzi/use/kodkod/transform/ocl/multi/mmContradictory.properties");
        KodkodValidateConfigurationAction cmd = new KodkodValidateConfigurationAction();
        cmd.initialize(session);
        cmd.extractConfigureAndValidate(f);

        System.out.println("Should be unsatisfiable");
    }





}
