package org.tzi.kodkod.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.config.impl.ClassConfigurator;
import org.tzi.kodkod.model.config.impl.Configurator;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.ObjectType;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Implementation of IClass.
 * 
 * @author Hendrik Reitmann
 */
public class Class extends ModelElement implements IClass {

	private static final Logger LOG = Logger.getLogger(Class.class);

	private boolean abstractC;
	private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>();
	private Set<IAssociation> associations = new HashSet<IAssociation>();
	private Map<String, IInvariant> invariants = new HashMap<String, IInvariant>();
	private Set<IClass> parents = new HashSet<IClass>();
	private Set<IClass> children = new HashSet<IClass>();
	private Relation inheritanceRelation;
	private ObjectType objectType;
	private Configurator<IClass> configurator;

	Class(IModel model, String name, boolean abstractC) {
		super(model, name);
		this.abstractC = abstractC;

		relation = Relation.unary(name());

		objectType = new ObjectType(this);
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return configurator.lowerBound(this, 1, tupleFactory);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return configurator.upperBound(this, 1, tupleFactory);
	}

	@Override
	public void addAttribute(IAttribute attribute) {
		attributes.put(attribute.name(), attribute);
	}

	@Override
	public Collection<IAttribute> attributes() {
		return attributes.values();
	}

	@Override
	public Collection<IAttribute> allAttributes() {
		Set<IAttribute> allAttributes = new HashSet<IAttribute>(attributes.values());
		for (IClass parent : parents) {
			allAttributes.addAll(parent.allAttributes());
		}
		return allAttributes;
	}

	@Override
	public IAttribute getAttribute(String name) {
		IAttribute attribute = attributes.get(name);
		if (attribute == null) {
			for (IClass parent : parents) {
				if (parent.getAttribute(name) != null) {
					attribute = parent.getAttribute(name);
					break;
				}
			}
		}
		return attribute;
	}

	@Override
	public void addAssociation(IAssociation association) {
		associations.add(association);
	}
	
	@Override
	public Collection<IAssociation> associations() {
		return associations;
	}
	
	@Override
	public Collection<IAssociation> allAssociations() {
		Set<IAssociation> res = new HashSet<IAssociation>();
		res.addAll(associations());
		
		for(IClass cls : allParents()){
			res.addAll(cls.associations());
		}
		return res;
	}
	
	@Override
	public void addInvariant(IInvariant invariant) {
		invariants.put(invariant.name(), invariant);
	}

	@Override
	public Collection<IInvariant> invariants() {
		return invariants.values();
	}

	@Override
	public Collection<IInvariant> allInvariants() {
		Set<IInvariant> allInvariants = new HashSet<IInvariant>(invariants.values());
		for (IClass parent : parents) {
			allInvariants.addAll(parent.allInvariants());
		}
		return allInvariants;
	}

	@Override
	public IInvariant getInvariant(String name) {
		return invariants.get(name);
	}

	@Override
	public void addParent(IClass parent) {
		parents.add(parent);
	}

	@Override
	public Collection<IClass> parents() {
		return parents;
	}
	
	@Override
	public Collection<IClass> allParents() {
		Set<IClass> parents = new HashSet<IClass>();
		parents.addAll(parents());
		
		for(IClass p : parents()){
			parents.addAll(p.allParents());
		}
		return parents;
	}
	
	@Override
	public void addChild(IClass child) {
		children.add(child);
	}

	@Override
	public Collection<IClass> children() {
		return children;
	}

	@Override
	public Collection<IClass> allChildren() {
		Set<IClass> children = new HashSet<IClass>();
		children.addAll(children());
		
		for(IClass p : children()){
			children.addAll(p.allChildren());
		}
		return children;
	}
	
	@Override
	public boolean isAbstract() {
		return abstractC;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitClass(this);
	}

	@Override
	public Formula constraints() {
		Formula formula = forbiddingSharingDefinition();
		if (existsInheritance()) {
			formula = formula.and(inheritanceDefinition());
		}
		return formula.and(configurator.constraints(this));
	}

	@Override
	public boolean existsInheritance() {
		return children.size() > 0;
	}

	@Override
	public Relation inheritanceRelation() {
		if (existsInheritance() && inheritanceRelation == null) {
			inheritanceRelation = Relation.unary(name() + "_inh");
		}
		return inheritanceRelation;
	}

	@Override
	public TupleSet inheritanceLowerBound(TupleFactory tupleFactory) {
		TupleSet inheritanceLowerBound = tupleFactory.noneOf(1);
		inheritanceLowerBound.addAll(lowerBound(tupleFactory));
		for (IClass clazz : children) {
			inheritanceLowerBound.addAll(clazz.inheritanceLowerBound(tupleFactory));
		}

		return inheritanceLowerBound;
	}

	@Override
	public TupleSet inheritanceUpperBound(TupleFactory tupleFactory) {
		TupleSet inheritanceUpperBound = tupleFactory.noneOf(1);
		inheritanceUpperBound.addAll(upperBound(tupleFactory));
		for (IClass clazz : children) {
			inheritanceUpperBound.addAll(clazz.inheritanceUpperBound(tupleFactory));
		}

		return inheritanceUpperBound;
	}

	/**
	 * Returns the formula for the generalization constraint.
	 * 
	 * @return
	 */
	private Formula inheritanceDefinition() {
		Expression expression = relation();

		for (IClass clazz : children) {
			if (clazz.existsInheritance()) {
				expression = expression.union(clazz.inheritanceRelation());
			} else {
				expression = expression.union(clazz.relation());
			}
		}

		Formula formula = inheritanceRelation().eq(expression);

		LOG.debug("Inheritance for " + name() + ": " + PrintHelper.prettyKodkod(formula));
		return formula;
	}
	
	private Formula forbiddingSharingDefinition() {
		List<IAssociation> assocs = new ArrayList<IAssociation>();
		
		/*
		 * Collect all associations that fulfill all of the following properties:
		 * - binary
		 * - not an association class
		 * - is a composition
		 * - part navigation points to this class
		 */
		for (IAssociation assoc : model.associations()) {
			if(assoc.isBinaryAssociation() && assoc.associationClass() == null
					&& assoc.associationEnds().get(0).aggregationKind() == IAssociationEnd.COMPOSITION
					&& assoc.associationEnds().get(1).associatedClass().equals(this)){
				assocs.add(assoc);
			}
		}
		
		if(assocs.size() < 2){
			return Formula.TRUE;
		}
		
		/*
		 * all self : (inh)Relation | (join( self, composition1 ) = undefined and ... and join( self, compositionN ) = undefined)
		 *                              or (join( self, composition1 ) <> undefined and ... and join( self, compositionN ) = undefined)
		 *                              ...
		 *                              or (join( self, composition1 ) = undefined and ... and join( self, compositionN ) <> undefined)
		 */
		final Expression undefined = model.typeFactory().undefinedType().relation();
		final Variable self = Variable.unary("self");
		final Expression rel = existsInheritance() ? inheritanceRelation() : relation();
		
		Formula matrix = null;
		
		for(int i = 0; i < assocs.size()+1; i++){
			Formula current = Formula.TRUE;
			for(int j = 0; j < assocs.size(); j++){
				IAssociation assoc = assocs.get(j);
				Expression joined = assoc.relation().join(self);
				
				if(i == j){
					current = current.and(joined.eq(undefined).not());
				} else {
					current = current.and(joined.eq(undefined));
				}
			}
			matrix = (matrix == null) ? current : matrix.or(current);
		}
		
		Decls vars = self.oneOf(rel);
		return matrix.forAll(vars);
	}
	
	@Override
	public ObjectType objectType() {
		return objectType;
	}

	@Override
	public void setConfigurator(Configurator<IClass> configurator) {
		this.configurator = configurator;
	}

	@Override
	public Configurator<IClass> getConfigurator() {
		return configurator;
	}

	@Override
	public void resetConfigurator() {
		configurator = new ClassConfigurator();
	}
}
