/* 
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.transpiler.util;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.jsweet.transpiler.JSweetContext;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

/**
 * A utility scanner that grabs all references to types used within a code tree.
 * 
 * @author Renaud Pawlak
 * @author Louis Grignon
 */
public class ReferenceGrabber extends TreeScanner<Void, Trees> {

	/**
	 * The grabbed references.
	 */
	public final Set<TypeMirror> referencedTypes = new HashSet<>();
	private final JSweetContext context;
	private final CompilationUnitTree compilationUnit;

	public ReferenceGrabber(JSweetContext context, CompilationUnitTree compilationUnit) {
		this.context = context;
		this.compilationUnit = compilationUnit;
	}

	/**
	 * Grab references on the given new-class tree.
	 */
	@Override
	public Void visitNewClass(NewClassTree newClass, Trees trees) {
		add(context.util.getTypeForTree(newClass.getIdentifier(), compilationUnit));

		return super.visitNewClass(newClass, trees);
	}

	/**
	 * Grab references on the given field-access tree.
	 */
	@Override
	public Void visitMemberSelect(MemberSelectTree memberSelectTree, Trees trees) {
		TypeMirror typeOfSelected = context.util.getTypeForTree(memberSelectTree.getExpression(), compilationUnit);
		if (typeOfSelected != null && typeOfSelected.getKind() == TypeKind.DECLARED) {
			add(typeOfSelected);
		}
		return super.visitMemberSelect(memberSelectTree, trees);
	}

	private void add(TypeMirror type) {
		referencedTypes.add(type);
	}
}
