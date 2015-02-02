package org.tzi.use.kodkod.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.config.Options;
import org.tzi.use.kodkod.UseCTScrollingKodkodModelValidator;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.kodkod.transform.ocl.DefaultExpressionVisitor;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.VarBindings;

/**
 * Cmd-Class for the scrolling in the solutions using classifier terms.
 *
 * @author Frank Hilken
 */
public class KodkodCTScrollingValidateCmd extends KodkodScrollingValidateCmd {

	@Override
	protected void noArguments() {
		LOG.info(LogMessages.pagingCmdError);
	}

	@Override
	protected void handleArguments(String arguments) {
		arguments = arguments.trim();
		
		if (arguments.equalsIgnoreCase("next")) {
			if (checkValidatorPresent()) {
				validator.nextSolution();
			}
		} else if (arguments.equalsIgnoreCase("previous")) {
			if (checkValidatorPresent()) {
				validator.previousSolution();
			}
		} else {
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*(\\d+)\\s*\\)", Pattern.CASE_INSENSITIVE);
			Matcher m = showPattern.matcher(arguments);
			
			if (m.matches()) {
				if (checkValidatorPresent()) {
					int index = Integer.parseInt(m.group(1));
					validator.showSolution(index);
				}
			} else {
				String fileToOpen = Shell.getInstance().getFilenameToOpen(arguments, false);
				fileToOpen = Options.getFilenameToOpen(fileToOpen);
				File file = new File(fileToOpen);
	
				if (file.exists() && file.canRead() && !file.isDirectory()) {
					
					resetValidator();
					try {
						if(!readObservationTermAndSetValidator()){
							System.out.println("Aborting.");
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					
					extractConfigureAndValidate(file);
				} else {
					LOG.error(LogMessages.pagingCmdError);
				}
			}
		}
	}

	private boolean readObservationTermAndSetValidator() throws IOException {
		Expression result = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		UseCTScrollingKodkodModelValidator v = (UseCTScrollingKodkodModelValidator) validator;
		int terms = 1;
		System.out.println("Input classifying terms (leave empty to abort, type `v' or `validate' to start validation)");
		
		do {
			System.out.print("Term " + terms + ": ");
			String line = br.readLine();
			StringWriter err = new StringWriter();
			
			if(line.trim().isEmpty()){
				// abort
				return false;
			}
			else if(line.trim().equalsIgnoreCase("v") || line.trim().equalsIgnoreCase("validate")){
				break;
			}
				
			result = OCLCompiler.compileExpression(session.system().model(), line, "<classifying term>", new PrintWriter(err), new VarBindings());

			// error checking
			if(result == null){
				System.out.println(err.toString());
				System.out.println();
				continue;
			}
			
			if(!result.type().isTypeOfInteger() && !result.type().isTypeOfBoolean()){
				System.out.println("The expression must result in type `Boolean' or `Integer'.");
				System.out.println();
				continue;
			}
			
			// transform into kodkod
			Node obsTermKodkod;
			try {
				DefaultExpressionVisitor ev = new DefaultExpressionVisitor(
						PluginModelFactory.INSTANCE.getModel(session.system().model()),
						new HashMap<String, Node>(), new HashMap<String, IClass>(),
						new HashMap<String, Variable>(), new ArrayList<String>());
				result.processWithVisitor(ev);
				obsTermKodkod = (Node) ev.getObject();
			}
			catch(TransformationException ex){
				System.out.println("The expression cannot be transformed by the model validator.");
				System.out.println("Reason: " + ex.getMessage());
				System.out.println();
				continue;
			}
			
			// success
			v.addObservationTerm(result, obsTermKodkod);
			terms++;
		}
		while(true);
		
		return true;
	}
	
	@Override
	protected void resetValidator() {
		validator = new UseCTScrollingKodkodModelValidator(session);
	}
}