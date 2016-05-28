////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

/**
 * A factory for creating objects from package names and names.
 * @author Rick Giles
 * @author lkuehne
 */
public class PackageObjectFactory implements ModuleFactory {
    /** Logger for PackageObjectFactory. */
    private static final Log LOG = LogFactory.getLog(PackageObjectFactory.class);

    /** Log message when ignoring exception. */
    private static final String IGNORING_EXCEPTION_MESSAGE = "Keep looking, ignoring exception";

    /** Exception message when it is unable to create a class instance. */
    private static final String UNABLE_TO_INSTANTIATE_EXCEPTION_MESSAGE =
        "PackageObjectFactory.unableToInstantiateExceptionMessage";
    /** Map of CheckStyle objects for fast loading. */
    private static final Map<String, String> OBJECT_MAP = Maps.newHashMap();

    /** Separator to use in strings. */
    private static final String STRING_SEPARATOR = ", ";

    /** A list of package names to prepend to class names. */
    private final Set<String> packages;
    /** The class loader used to load Checkstyle core and custom modules. */
    private final ClassLoader moduleClassLoader;

    static {
        final String csPackage = "com.puppycrawl.tools.checkstyle.";

        OBJECT_MAP.put("NeedBracesCheck",
                csPackage + "checks.blocks.NeedBracesCheck");
        OBJECT_MAP.put("NeedBraces",
                csPackage + "checks.blocks.NeedBracesCheck");
        OBJECT_MAP.put("SingleSpaceSeparatorCheck",
                csPackage + "checks.whitespace.SingleSpaceSeparatorCheck");
        OBJECT_MAP.put("SingleSpaceSeparator",
                csPackage + "checks.whitespace.SingleSpaceSeparatorCheck");
        OBJECT_MAP.put("LineLengthCheck",
                csPackage + "checks.sizes.LineLengthCheck");
        OBJECT_MAP.put("LineLength",
                csPackage + "checks.sizes.LineLengthCheck");
        OBJECT_MAP.put("AnnotationUseStyleCheck",
                csPackage + "checks.annotation.AnnotationUseStyleCheck");
        OBJECT_MAP.put("AnnotationUseStyle",
                csPackage + "checks.annotation.AnnotationUseStyleCheck");
        OBJECT_MAP.put("NoWhitespaceAfterCheck",
                csPackage + "checks.whitespace.NoWhitespaceAfterCheck");
        OBJECT_MAP.put("NoWhitespaceAfter",
                csPackage + "checks.whitespace.NoWhitespaceAfterCheck");
        OBJECT_MAP.put("NPathComplexityCheck",
                csPackage + "checks.metrics.NPathComplexityCheck");
        OBJECT_MAP.put("NPathComplexity",
                csPackage + "checks.metrics.NPathComplexityCheck");
        OBJECT_MAP.put("HeaderCheck",
                csPackage + "checks.header.HeaderCheck");
        OBJECT_MAP.put("Header",
                csPackage + "checks.header.HeaderCheck");
        OBJECT_MAP.put("FileContentsHolder",
                csPackage + "checks.FileContentsHolder");
        OBJECT_MAP.put("SuperFinalizeCheck",
                csPackage + "checks.coding.SuperFinalizeCheck");
        OBJECT_MAP.put("SuperFinalize",
                csPackage + "checks.coding.SuperFinalizeCheck");
        OBJECT_MAP.put("SuppressionFilter",
                csPackage + "filters.SuppressionFilter");
        OBJECT_MAP.put("NoCloneCheck",
                csPackage + "checks.coding.NoCloneCheck");
        OBJECT_MAP.put("NoClone",
                csPackage + "checks.coding.NoCloneCheck");
        OBJECT_MAP.put("SuperCloneCheck",
                csPackage + "checks.coding.SuperCloneCheck");
        OBJECT_MAP.put("SuperClone",
                csPackage + "checks.coding.SuperCloneCheck");
        OBJECT_MAP.put("BooleanExpressionComplexityCheck",
                csPackage + "checks.metrics.BooleanExpressionComplexityCheck");
        OBJECT_MAP.put("BooleanExpressionComplexity",
                csPackage + "checks.metrics.BooleanExpressionComplexityCheck");
        OBJECT_MAP.put("ClassFanOutComplexityCheck",
                csPackage + "checks.metrics.ClassFanOutComplexityCheck");
        OBJECT_MAP.put("ClassFanOutComplexity",
                csPackage + "checks.metrics.ClassFanOutComplexityCheck");
        OBJECT_MAP.put("JavadocPackageCheck",
                csPackage + "checks.javadoc.JavadocPackageCheck");
        OBJECT_MAP.put("JavadocPackage",
                csPackage + "checks.javadoc.JavadocPackageCheck");
        OBJECT_MAP.put("EmptyBlockCheck",
                csPackage + "checks.blocks.EmptyBlockCheck");
        OBJECT_MAP.put("EmptyBlock",
                csPackage + "checks.blocks.EmptyBlockCheck");
        OBJECT_MAP.put("MutableExceptionCheck",
                csPackage + "checks.design.MutableExceptionCheck");
        OBJECT_MAP.put("MutableException",
                csPackage + "checks.design.MutableExceptionCheck");
        OBJECT_MAP.put("UnusedImportsCheck",
                csPackage + "checks.imports.UnusedImportsCheck");
        OBJECT_MAP.put("UnusedImports",
                csPackage + "checks.imports.UnusedImportsCheck");
        OBJECT_MAP.put("IllegalTokenCheck",
                csPackage + "checks.coding.IllegalTokenCheck");
        OBJECT_MAP.put("IllegalToken",
                csPackage + "checks.coding.IllegalTokenCheck");
        OBJECT_MAP.put("CommentsIndentationCheck",
                csPackage + "checks.indentation.CommentsIndentationCheck");
        OBJECT_MAP.put("CommentsIndentation",
                csPackage + "checks.indentation.CommentsIndentationCheck");
        OBJECT_MAP.put("FileTabCharacterCheck",
                csPackage + "checks.whitespace.FileTabCharacterCheck");
        OBJECT_MAP.put("FileTabCharacter",
                csPackage + "checks.whitespace.FileTabCharacterCheck");
        OBJECT_MAP.put("ParameterNameCheck",
                csPackage + "checks.naming.ParameterNameCheck");
        OBJECT_MAP.put("ParameterName",
                csPackage + "checks.naming.ParameterNameCheck");
        OBJECT_MAP.put("DefaultComesLastCheck",
                csPackage + "checks.coding.DefaultComesLastCheck");
        OBJECT_MAP.put("DefaultComesLast",
                csPackage + "checks.coding.DefaultComesLastCheck");
        OBJECT_MAP.put("ClassDataAbstractionCouplingCheck",
                csPackage + "checks.metrics.ClassDataAbstractionCouplingCheck");
        OBJECT_MAP.put("ClassDataAbstractionCoupling",
                csPackage + "checks.metrics.ClassDataAbstractionCouplingCheck");
        OBJECT_MAP.put("JavadocStyleCheck",
                csPackage + "checks.javadoc.JavadocStyleCheck");
        OBJECT_MAP.put("JavadocStyle",
                csPackage + "checks.javadoc.JavadocStyleCheck");
        OBJECT_MAP.put("IllegalTypeCheck",
                csPackage + "checks.coding.IllegalTypeCheck");
        OBJECT_MAP.put("IllegalType",
                csPackage + "checks.coding.IllegalTypeCheck");
        OBJECT_MAP.put("FinalParametersCheck",
                csPackage + "checks.FinalParametersCheck");
        OBJECT_MAP.put("FinalParameters",
                csPackage + "checks.FinalParametersCheck");
        OBJECT_MAP.put("PackageNameCheck",
                csPackage + "checks.naming.PackageNameCheck");
        OBJECT_MAP.put("PackageName",
                csPackage + "checks.naming.PackageNameCheck");
        OBJECT_MAP.put("RegexpMultilineCheck",
                csPackage + "checks.regexp.RegexpMultilineCheck");
        OBJECT_MAP.put("RegexpMultiline",
                csPackage + "checks.regexp.RegexpMultilineCheck");
        OBJECT_MAP.put("MagicNumberCheck",
                csPackage + "checks.coding.MagicNumberCheck");
        OBJECT_MAP.put("MagicNumber",
                csPackage + "checks.coding.MagicNumberCheck");
        OBJECT_MAP.put("LeftCurlyCheck",
                csPackage + "checks.blocks.LeftCurlyCheck");
        OBJECT_MAP.put("LeftCurly",
                csPackage + "checks.blocks.LeftCurlyCheck");
        OBJECT_MAP.put("LocalVariableNameCheck",
                csPackage + "checks.naming.LocalVariableNameCheck");
        OBJECT_MAP.put("LocalVariableName",
                csPackage + "checks.naming.LocalVariableNameCheck");
        OBJECT_MAP.put("AnnotationLocationCheck",
                csPackage + "checks.annotation.AnnotationLocationCheck");
        OBJECT_MAP.put("AnnotationLocation",
                csPackage + "checks.annotation.AnnotationLocationCheck");
        OBJECT_MAP.put("MissingOverrideCheck",
                csPackage + "checks.annotation.MissingOverrideCheck");
        OBJECT_MAP.put("MissingOverride",
                csPackage + "checks.annotation.MissingOverrideCheck");
        OBJECT_MAP.put("GenericWhitespaceCheck",
                csPackage + "checks.whitespace.GenericWhitespaceCheck");
        OBJECT_MAP.put("GenericWhitespace",
                csPackage + "checks.whitespace.GenericWhitespaceCheck");
        OBJECT_MAP.put("EqualsHashCodeCheck",
                csPackage + "checks.coding.EqualsHashCodeCheck");
        OBJECT_MAP.put("EqualsHashCode",
                csPackage + "checks.coding.EqualsHashCodeCheck");
        OBJECT_MAP.put("UpperEllCheck",
                csPackage + "checks.UpperEllCheck");
        OBJECT_MAP.put("UpperEll",
                csPackage + "checks.UpperEllCheck");
        OBJECT_MAP.put("TrailingCommentCheck",
                csPackage + "checks.TrailingCommentCheck");
        OBJECT_MAP.put("TrailingComment",
                csPackage + "checks.TrailingCommentCheck");
        OBJECT_MAP.put("RegexpOnFilenameCheck",
                csPackage + "checks.regexp.RegexpOnFilenameCheck");
        OBJECT_MAP.put("RegexpOnFilename",
                csPackage + "checks.regexp.RegexpOnFilenameCheck");
        OBJECT_MAP.put("JavadocTagContinuationIndentationCheck",
                csPackage + "checks.javadoc.JavadocTagContinuationIndentationCheck");
        OBJECT_MAP.put("JavadocTagContinuationIndentation",
                csPackage + "checks.javadoc.JavadocTagContinuationIndentationCheck");
        OBJECT_MAP.put("SuppressWarningsCheck",
                csPackage + "checks.annotation.SuppressWarningsCheck");
        OBJECT_MAP.put("SuppressWarnings",
                csPackage + "checks.annotation.SuppressWarningsCheck");
        OBJECT_MAP.put("LocalFinalVariableNameCheck",
                csPackage + "checks.naming.LocalFinalVariableNameCheck");
        OBJECT_MAP.put("LocalFinalVariableName",
                csPackage + "checks.naming.LocalFinalVariableNameCheck");
        OBJECT_MAP.put("JavadocVariableCheck",
                csPackage + "checks.javadoc.JavadocVariableCheck");
        OBJECT_MAP.put("JavadocVariable",
                csPackage + "checks.javadoc.JavadocVariableCheck");
        OBJECT_MAP.put("MethodTypeParameterNameCheck",
                csPackage + "checks.naming.MethodTypeParameterNameCheck");
        OBJECT_MAP.put("MethodTypeParameterName",
                csPackage + "checks.naming.MethodTypeParameterNameCheck");
        OBJECT_MAP.put("RedundantModifierCheck",
                csPackage + "checks.modifier.RedundantModifierCheck");
        OBJECT_MAP.put("RedundantModifier",
                csPackage + "checks.modifier.RedundantModifierCheck");
        OBJECT_MAP.put("ThrowsCountCheck",
                csPackage + "checks.design.ThrowsCountCheck");
        OBJECT_MAP.put("ThrowsCount",
                csPackage + "checks.design.ThrowsCountCheck");
        OBJECT_MAP.put("VisibilityModifierCheck",
                csPackage + "checks.design.VisibilityModifierCheck");
        OBJECT_MAP.put("VisibilityModifier",
                csPackage + "checks.design.VisibilityModifierCheck");
        OBJECT_MAP.put("IllegalImportCheck",
                csPackage + "checks.imports.IllegalImportCheck");
        OBJECT_MAP.put("IllegalImport",
                csPackage + "checks.imports.IllegalImportCheck");
        OBJECT_MAP.put("ImportControlCheck",
                csPackage + "checks.imports.ImportControlCheck");
        OBJECT_MAP.put("ImportControl",
                csPackage + "checks.imports.ImportControlCheck");
        OBJECT_MAP.put("StaticVariableNameCheck",
                csPackage + "checks.naming.StaticVariableNameCheck");
        OBJECT_MAP.put("StaticVariableName",
                csPackage + "checks.naming.StaticVariableNameCheck");
        OBJECT_MAP.put("AvoidNestedBlocksCheck",
                csPackage + "checks.blocks.AvoidNestedBlocksCheck");
        OBJECT_MAP.put("AvoidNestedBlocks",
                csPackage + "checks.blocks.AvoidNestedBlocksCheck");
        OBJECT_MAP.put("AvoidStarImportCheck",
                csPackage + "checks.imports.AvoidStarImportCheck");
        OBJECT_MAP.put("AvoidStarImport",
                csPackage + "checks.imports.AvoidStarImportCheck");
        OBJECT_MAP.put("RegexpHeaderCheck",
                csPackage + "checks.header.RegexpHeaderCheck");
        OBJECT_MAP.put("RegexpHeader",
                csPackage + "checks.header.RegexpHeaderCheck");
        OBJECT_MAP.put("TranslationCheck",
                csPackage + "checks.TranslationCheck");
        OBJECT_MAP.put("Translation",
                csPackage + "checks.TranslationCheck");
        OBJECT_MAP.put("EmptyStatementCheck",
                csPackage + "checks.coding.EmptyStatementCheck");
        OBJECT_MAP.put("EmptyStatement",
                csPackage + "checks.coding.EmptyStatementCheck");
        OBJECT_MAP.put("FallThroughCheck",
                csPackage + "checks.coding.FallThroughCheck");
        OBJECT_MAP.put("FallThrough",
                csPackage + "checks.coding.FallThroughCheck");
        OBJECT_MAP.put("RightCurlyCheck",
                csPackage + "checks.blocks.RightCurlyCheck");
        OBJECT_MAP.put("RightCurly",
                csPackage + "checks.blocks.RightCurlyCheck");
        OBJECT_MAP.put("RegexpSinglelineCheck",
                csPackage + "checks.regexp.RegexpSinglelineCheck");
        OBJECT_MAP.put("RegexpSingleline",
                csPackage + "checks.regexp.RegexpSinglelineCheck");
        OBJECT_MAP.put("JavaNCSSCheck",
                csPackage + "checks.metrics.JavaNCSSCheck");
        OBJECT_MAP.put("JavaNCSS",
                csPackage + "checks.metrics.JavaNCSSCheck");
        OBJECT_MAP.put("ReturnCountCheck",
                csPackage + "checks.coding.ReturnCountCheck");
        OBJECT_MAP.put("ReturnCount",
                csPackage + "checks.coding.ReturnCountCheck");
        OBJECT_MAP.put("HiddenFieldCheck",
                csPackage + "checks.coding.HiddenFieldCheck");
        OBJECT_MAP.put("HiddenField",
                csPackage + "checks.coding.HiddenFieldCheck");
        OBJECT_MAP.put("FileLengthCheck",
                csPackage + "checks.sizes.FileLengthCheck");
        OBJECT_MAP.put("FileLength",
                csPackage + "checks.sizes.FileLengthCheck");
        OBJECT_MAP.put("NestedForDepthCheck",
                csPackage + "checks.coding.NestedForDepthCheck");
        OBJECT_MAP.put("NestedForDepth",
                csPackage + "checks.coding.NestedForDepthCheck");
        OBJECT_MAP.put("ExecutableStatementCountCheck",
                csPackage + "checks.sizes.ExecutableStatementCountCheck");
        OBJECT_MAP.put("ExecutableStatementCount",
                csPackage + "checks.sizes.ExecutableStatementCountCheck");
        OBJECT_MAP.put("MethodNameCheck",
                csPackage + "checks.naming.MethodNameCheck");
        OBJECT_MAP.put("MethodName",
                csPackage + "checks.naming.MethodNameCheck");
        OBJECT_MAP.put("MissingCtorCheck",
                csPackage + "checks.coding.MissingCtorCheck");
        OBJECT_MAP.put("MissingCtor",
                csPackage + "checks.coding.MissingCtorCheck");
        OBJECT_MAP.put("SingleLineJavadocCheck",
                csPackage + "checks.javadoc.SingleLineJavadocCheck");
        OBJECT_MAP.put("SingleLineJavadoc",
                csPackage + "checks.javadoc.SingleLineJavadocCheck");
        OBJECT_MAP.put("ParenPadCheck",
                csPackage + "checks.whitespace.ParenPadCheck");
        OBJECT_MAP.put("ParenPad",
                csPackage + "checks.whitespace.ParenPadCheck");
        OBJECT_MAP.put("MethodCountCheck",
                csPackage + "checks.sizes.MethodCountCheck");
        OBJECT_MAP.put("MethodCount",
                csPackage + "checks.sizes.MethodCountCheck");
        OBJECT_MAP.put("MemberNameCheck",
                csPackage + "checks.naming.MemberNameCheck");
        OBJECT_MAP.put("MemberName",
                csPackage + "checks.naming.MemberNameCheck");
        OBJECT_MAP.put("EmptyCatchBlockCheck",
                csPackage + "checks.blocks.EmptyCatchBlockCheck");
        OBJECT_MAP.put("EmptyCatchBlock",
                csPackage + "checks.blocks.EmptyCatchBlockCheck");
        OBJECT_MAP.put("SuppressWarningsHolder",
                csPackage + "checks.SuppressWarningsHolder");
        OBJECT_MAP.put("RegexpSinglelineJavaCheck",
                csPackage + "checks.regexp.RegexpSinglelineJavaCheck");
        OBJECT_MAP.put("RegexpSinglelineJava",
                csPackage + "checks.regexp.RegexpSinglelineJavaCheck");
        OBJECT_MAP.put("IllegalCatchCheck",
                csPackage + "checks.coding.IllegalCatchCheck");
        OBJECT_MAP.put("IllegalCatch",
                csPackage + "checks.coding.IllegalCatchCheck");
        OBJECT_MAP.put("ArrayTypeStyleCheck",
                csPackage + "checks.ArrayTypeStyleCheck");
        OBJECT_MAP.put("ArrayTypeStyle",
                csPackage + "checks.ArrayTypeStyleCheck");
        OBJECT_MAP.put("SeparatorWrapCheck",
                csPackage + "checks.whitespace.SeparatorWrapCheck");
        OBJECT_MAP.put("SeparatorWrap",
                csPackage + "checks.whitespace.SeparatorWrapCheck");
        OBJECT_MAP.put("TodoCommentCheck",
                csPackage + "checks.TodoCommentCheck");
        OBJECT_MAP.put("TodoComment",
                csPackage + "checks.TodoCommentCheck");
        OBJECT_MAP.put("CyclomaticComplexityCheck",
                csPackage + "checks.metrics.CyclomaticComplexityCheck");
        OBJECT_MAP.put("CyclomaticComplexity",
                csPackage + "checks.metrics.CyclomaticComplexityCheck");
        OBJECT_MAP.put("PackageDeclarationCheck",
                csPackage + "checks.coding.PackageDeclarationCheck");
        OBJECT_MAP.put("PackageDeclaration",
                csPackage + "checks.coding.PackageDeclarationCheck");
        OBJECT_MAP.put("ArrayTrailingCommaCheck",
                csPackage + "checks.coding.ArrayTrailingCommaCheck");
        OBJECT_MAP.put("ArrayTrailingComma",
                csPackage + "checks.coding.ArrayTrailingCommaCheck");
        OBJECT_MAP.put("RequireThisCheck",
                csPackage + "checks.coding.RequireThisCheck");
        OBJECT_MAP.put("RequireThis",
                csPackage + "checks.coding.RequireThisCheck");
        OBJECT_MAP.put("UncommentedMainCheck",
                csPackage + "checks.UncommentedMainCheck");
        OBJECT_MAP.put("UncommentedMain",
                csPackage + "checks.UncommentedMainCheck");
        OBJECT_MAP.put("OuterTypeFilenameCheck",
                csPackage + "checks.OuterTypeFilenameCheck");
        OBJECT_MAP.put("OuterTypeFilename",
                csPackage + "checks.OuterTypeFilenameCheck");
        OBJECT_MAP.put("StringLiteralEqualityCheck",
                csPackage + "checks.coding.StringLiteralEqualityCheck");
        OBJECT_MAP.put("StringLiteralEquality",
                csPackage + "checks.coding.StringLiteralEqualityCheck");
        OBJECT_MAP.put("OuterTypeNumberCheck",
                csPackage + "checks.sizes.OuterTypeNumberCheck");
        OBJECT_MAP.put("OuterTypeNumber",
                csPackage + "checks.sizes.OuterTypeNumberCheck");
        OBJECT_MAP.put("MethodParamPadCheck",
                csPackage + "checks.whitespace.MethodParamPadCheck");
        OBJECT_MAP.put("MethodParamPad",
                csPackage + "checks.whitespace.MethodParamPadCheck");
        OBJECT_MAP.put("VariableDeclarationUsageDistanceCheck",
                csPackage + "checks.coding.VariableDeclarationUsageDistanceCheck");
        OBJECT_MAP.put("VariableDeclarationUsageDistance",
                csPackage + "checks.coding.VariableDeclarationUsageDistanceCheck");
        OBJECT_MAP.put("WhitespaceAfterCheck",
                csPackage + "checks.whitespace.WhitespaceAfterCheck");
        OBJECT_MAP.put("WhitespaceAfter",
                csPackage + "checks.whitespace.WhitespaceAfterCheck");
        OBJECT_MAP.put("OneTopLevelClassCheck",
                csPackage + "checks.design.OneTopLevelClassCheck");
        OBJECT_MAP.put("OneTopLevelClass",
                csPackage + "checks.design.OneTopLevelClassCheck");
        OBJECT_MAP.put("JavadocTypeCheck",
                csPackage + "checks.javadoc.JavadocTypeCheck");
        OBJECT_MAP.put("JavadocType",
                csPackage + "checks.javadoc.JavadocTypeCheck");
        OBJECT_MAP.put("IllegalInstantiationCheck",
                csPackage + "checks.coding.IllegalInstantiationCheck");
        OBJECT_MAP.put("IllegalInstantiation",
                csPackage + "checks.coding.IllegalInstantiationCheck");
        OBJECT_MAP.put("DescendantTokenCheck",
                csPackage + "checks.DescendantTokenCheck");
        OBJECT_MAP.put("DescendantToken",
                csPackage + "checks.DescendantTokenCheck");
        OBJECT_MAP.put("WriteTagCheck",
                csPackage + "checks.javadoc.WriteTagCheck");
        OBJECT_MAP.put("WriteTag",
                csPackage + "checks.javadoc.WriteTagCheck");
        OBJECT_MAP.put("ParameterNumberCheck",
                csPackage + "checks.sizes.ParameterNumberCheck");
        OBJECT_MAP.put("ParameterNumber",
                csPackage + "checks.sizes.ParameterNumberCheck");
        OBJECT_MAP.put("RedundantImportCheck",
                csPackage + "checks.imports.RedundantImportCheck");
        OBJECT_MAP.put("RedundantImport",
                csPackage + "checks.imports.RedundantImportCheck");
        OBJECT_MAP.put("InterfaceIsTypeCheck",
                csPackage + "checks.design.InterfaceIsTypeCheck");
        OBJECT_MAP.put("InterfaceIsType",
                csPackage + "checks.design.InterfaceIsTypeCheck");
        OBJECT_MAP.put("CatchParameterNameCheck",
                csPackage + "checks.naming.CatchParameterNameCheck");
        OBJECT_MAP.put("CatchParameterName",
                csPackage + "checks.naming.CatchParameterNameCheck");
        OBJECT_MAP.put("NewlineAtEndOfFileCheck",
                csPackage + "checks.NewlineAtEndOfFileCheck");
        OBJECT_MAP.put("NewlineAtEndOfFile",
                csPackage + "checks.NewlineAtEndOfFileCheck");
        OBJECT_MAP.put("NoWhitespaceBeforeCheck",
                csPackage + "checks.whitespace.NoWhitespaceBeforeCheck");
        OBJECT_MAP.put("NoWhitespaceBefore",
                csPackage + "checks.whitespace.NoWhitespaceBeforeCheck");
        OBJECT_MAP.put("EmptyForIteratorPadCheck",
                csPackage + "checks.whitespace.EmptyForIteratorPadCheck");
        OBJECT_MAP.put("EmptyForIteratorPad",
                csPackage + "checks.whitespace.EmptyForIteratorPadCheck");
        OBJECT_MAP.put("NoLineWrapCheck",
                csPackage + "checks.whitespace.NoLineWrapCheck");
        OBJECT_MAP.put("NoLineWrap",
                csPackage + "checks.whitespace.NoLineWrapCheck");
        OBJECT_MAP.put("ModifierOrderCheck",
                csPackage + "checks.modifier.ModifierOrderCheck");
        OBJECT_MAP.put("ModifierOrder",
                csPackage + "checks.modifier.ModifierOrderCheck");
        OBJECT_MAP.put("MissingSwitchDefaultCheck",
                csPackage + "checks.coding.MissingSwitchDefaultCheck");
        OBJECT_MAP.put("MissingSwitchDefault",
                csPackage + "checks.coding.MissingSwitchDefaultCheck");
        OBJECT_MAP.put("JavadocMethodCheck",
                csPackage + "checks.javadoc.JavadocMethodCheck");
        OBJECT_MAP.put("JavadocMethod",
                csPackage + "checks.javadoc.JavadocMethodCheck");
        OBJECT_MAP.put("CovariantEqualsCheck",
                csPackage + "checks.coding.CovariantEqualsCheck");
        OBJECT_MAP.put("CovariantEquals",
                csPackage + "checks.coding.CovariantEqualsCheck");
        OBJECT_MAP.put("WhitespaceAroundCheck",
                csPackage + "checks.whitespace.WhitespaceAroundCheck");
        OBJECT_MAP.put("WhitespaceAround",
                csPackage + "checks.whitespace.WhitespaceAroundCheck");
        OBJECT_MAP.put("InterfaceTypeParameterNameCheck",
                csPackage + "checks.naming.InterfaceTypeParameterNameCheck");
        OBJECT_MAP.put("InterfaceTypeParameterName",
                csPackage + "checks.naming.InterfaceTypeParameterNameCheck");
        OBJECT_MAP.put("SummaryJavadocCheck",
                csPackage + "checks.javadoc.SummaryJavadocCheck");
        OBJECT_MAP.put("SummaryJavadoc",
                csPackage + "checks.javadoc.SummaryJavadocCheck");
        OBJECT_MAP.put("IllegalTokenTextCheck",
                csPackage + "checks.coding.IllegalTokenTextCheck");
        OBJECT_MAP.put("IllegalTokenText",
                csPackage + "checks.coding.IllegalTokenTextCheck");
        OBJECT_MAP.put("PackageAnnotationCheck",
                csPackage + "checks.annotation.PackageAnnotationCheck");
        OBJECT_MAP.put("PackageAnnotation",
                csPackage + "checks.annotation.PackageAnnotationCheck");
        OBJECT_MAP.put("AvoidStaticImportCheck",
                csPackage + "checks.imports.AvoidStaticImportCheck");
        OBJECT_MAP.put("AvoidStaticImport",
                csPackage + "checks.imports.AvoidStaticImportCheck");
        OBJECT_MAP.put("IndentationCheck",
                csPackage + "checks.indentation.IndentationCheck");
        OBJECT_MAP.put("Indentation",
                csPackage + "checks.indentation.IndentationCheck");
        OBJECT_MAP.put("FinalClassCheck",
                csPackage + "checks.design.FinalClassCheck");
        OBJECT_MAP.put("FinalClass",
                csPackage + "checks.design.FinalClassCheck");
        OBJECT_MAP.put("CustomImportOrderCheck",
                csPackage + "checks.imports.CustomImportOrderCheck");
        OBJECT_MAP.put("CustomImportOrder",
                csPackage + "checks.imports.CustomImportOrderCheck");
        OBJECT_MAP.put("SuppressionCommentFilter",
                csPackage + "filters.SuppressionCommentFilter");
        OBJECT_MAP.put("InnerAssignmentCheck",
                csPackage + "checks.coding.InnerAssignmentCheck");
        OBJECT_MAP.put("InnerAssignment",
                csPackage + "checks.coding.InnerAssignmentCheck");
        OBJECT_MAP.put("EqualsAvoidNullCheck",
                csPackage + "checks.coding.EqualsAvoidNullCheck");
        OBJECT_MAP.put("EqualsAvoidNull",
                csPackage + "checks.coding.EqualsAvoidNullCheck");
        OBJECT_MAP.put("SuppressWithNearbyCommentFilter",
                csPackage + "filters.SuppressWithNearbyCommentFilter");
        OBJECT_MAP.put("MissingDeprecatedCheck",
                csPackage + "checks.annotation.MissingDeprecatedCheck");
        OBJECT_MAP.put("MissingDeprecated",
                csPackage + "checks.annotation.MissingDeprecatedCheck");
        OBJECT_MAP.put("DesignForExtensionCheck",
                csPackage + "checks.design.DesignForExtensionCheck");
        OBJECT_MAP.put("DesignForExtension",
                csPackage + "checks.design.DesignForExtensionCheck");
        OBJECT_MAP.put("ClassTypeParameterNameCheck",
                csPackage + "checks.naming.ClassTypeParameterNameCheck");
        OBJECT_MAP.put("ClassTypeParameterName",
                csPackage + "checks.naming.ClassTypeParameterNameCheck");
        OBJECT_MAP.put("UnnecessaryParenthesesCheck",
                csPackage + "checks.coding.UnnecessaryParenthesesCheck");
        OBJECT_MAP.put("UnnecessaryParentheses",
                csPackage + "checks.coding.UnnecessaryParenthesesCheck");
        OBJECT_MAP.put("AbstractClassNameCheck",
                csPackage + "checks.naming.AbstractClassNameCheck");
        OBJECT_MAP.put("AbstractClassName",
                csPackage + "checks.naming.AbstractClassNameCheck");
        OBJECT_MAP.put("ExplicitInitializationCheck",
                csPackage + "checks.coding.ExplicitInitializationCheck");
        OBJECT_MAP.put("ExplicitInitialization",
                csPackage + "checks.coding.ExplicitInitializationCheck");
        OBJECT_MAP.put("NoFinalizerCheck",
                csPackage + "checks.coding.NoFinalizerCheck");
        OBJECT_MAP.put("NoFinalizer",
                csPackage + "checks.coding.NoFinalizerCheck");
        OBJECT_MAP.put("FinalLocalVariableCheck",
                csPackage + "checks.coding.FinalLocalVariableCheck");
        OBJECT_MAP.put("FinalLocalVariable",
                csPackage + "checks.coding.FinalLocalVariableCheck");
        OBJECT_MAP.put("MultipleVariableDeclarationsCheck",
                csPackage + "checks.coding.MultipleVariableDeclarationsCheck");
        OBJECT_MAP.put("MultipleVariableDeclarations",
                csPackage + "checks.coding.MultipleVariableDeclarationsCheck");
        OBJECT_MAP.put("AtclauseOrderCheck",
                csPackage + "checks.javadoc.AtclauseOrderCheck");
        OBJECT_MAP.put("AtclauseOrder",
                csPackage + "checks.javadoc.AtclauseOrderCheck");
        OBJECT_MAP.put("InnerTypeLastCheck",
                csPackage + "checks.design.InnerTypeLastCheck");
        OBJECT_MAP.put("InnerTypeLast",
                csPackage + "checks.design.InnerTypeLastCheck");
        OBJECT_MAP.put("AvoidEscapedUnicodeCharactersCheck",
                csPackage + "checks.AvoidEscapedUnicodeCharactersCheck");
        OBJECT_MAP.put("AvoidEscapedUnicodeCharacters",
                csPackage + "checks.AvoidEscapedUnicodeCharactersCheck");
        OBJECT_MAP.put("MultipleStringLiteralsCheck",
                csPackage + "checks.coding.MultipleStringLiteralsCheck");
        OBJECT_MAP.put("MultipleStringLiterals",
                csPackage + "checks.coding.MultipleStringLiteralsCheck");
        OBJECT_MAP.put("ModifiedControlVariableCheck",
                csPackage + "checks.coding.ModifiedControlVariableCheck");
        OBJECT_MAP.put("ModifiedControlVariable",
                csPackage + "checks.coding.ModifiedControlVariableCheck");
        OBJECT_MAP.put("SeverityMatchFilter",
                csPackage + "filters.SeverityMatchFilter");
        OBJECT_MAP.put("RegexpCheck",
                csPackage + "checks.regexp.RegexpCheck");
        OBJECT_MAP.put("Regexp",
                csPackage + "checks.regexp.RegexpCheck");
        OBJECT_MAP.put("OneStatementPerLineCheck",
                csPackage + "checks.coding.OneStatementPerLineCheck");
        OBJECT_MAP.put("OneStatementPerLine",
                csPackage + "checks.coding.OneStatementPerLineCheck");
        OBJECT_MAP.put("MethodLengthCheck",
                csPackage + "checks.sizes.MethodLengthCheck");
        OBJECT_MAP.put("MethodLength",
                csPackage + "checks.sizes.MethodLengthCheck");
        OBJECT_MAP.put("OperatorWrapCheck",
                csPackage + "checks.whitespace.OperatorWrapCheck");
        OBJECT_MAP.put("OperatorWrap",
                csPackage + "checks.whitespace.OperatorWrapCheck");
        OBJECT_MAP.put("IllegalThrowsCheck",
                csPackage + "checks.coding.IllegalThrowsCheck");
        OBJECT_MAP.put("IllegalThrows",
                csPackage + "checks.coding.IllegalThrowsCheck");
        OBJECT_MAP.put("AbbreviationAsWordInNameCheck",
                csPackage + "checks.naming.AbbreviationAsWordInNameCheck");
        OBJECT_MAP.put("AbbreviationAsWordInName",
                csPackage + "checks.naming.AbbreviationAsWordInNameCheck");
        OBJECT_MAP.put("NestedTryDepthCheck",
                csPackage + "checks.coding.NestedTryDepthCheck");
        OBJECT_MAP.put("NestedTryDepth",
                csPackage + "checks.coding.NestedTryDepthCheck");
        OBJECT_MAP.put("UniquePropertiesCheck",
                csPackage + "checks.UniquePropertiesCheck");
        OBJECT_MAP.put("UniqueProperties",
                csPackage + "checks.UniquePropertiesCheck");
        OBJECT_MAP.put("EmptyLineSeparatorCheck",
                csPackage + "checks.whitespace.EmptyLineSeparatorCheck");
        OBJECT_MAP.put("EmptyLineSeparator",
                csPackage + "checks.whitespace.EmptyLineSeparatorCheck");
        OBJECT_MAP.put("ParameterAssignmentCheck",
                csPackage + "checks.coding.ParameterAssignmentCheck");
        OBJECT_MAP.put("ParameterAssignment",
                csPackage + "checks.coding.ParameterAssignmentCheck");
        OBJECT_MAP.put("JavadocParagraphCheck",
                csPackage + "checks.javadoc.JavadocParagraphCheck");
        OBJECT_MAP.put("JavadocParagraph",
                csPackage + "checks.javadoc.JavadocParagraphCheck");
        OBJECT_MAP.put("OverloadMethodsDeclarationOrderCheck",
                csPackage + "checks.coding.OverloadMethodsDeclarationOrderCheck");
        OBJECT_MAP.put("OverloadMethodsDeclarationOrder",
                csPackage + "checks.coding.OverloadMethodsDeclarationOrderCheck");
        OBJECT_MAP.put("AvoidInlineConditionalsCheck",
                csPackage + "checks.coding.AvoidInlineConditionalsCheck");
        OBJECT_MAP.put("AvoidInlineConditionals",
                csPackage + "checks.coding.AvoidInlineConditionalsCheck");
        OBJECT_MAP.put("NonEmptyAtclauseDescriptionCheck",
                csPackage + "checks.javadoc.NonEmptyAtclauseDescriptionCheck");
        OBJECT_MAP.put("NonEmptyAtclauseDescription",
                csPackage + "checks.javadoc.NonEmptyAtclauseDescriptionCheck");
        OBJECT_MAP.put("SimplifyBooleanReturnCheck",
                csPackage + "checks.coding.SimplifyBooleanReturnCheck");
        OBJECT_MAP.put("SimplifyBooleanReturn",
                csPackage + "checks.coding.SimplifyBooleanReturnCheck");
        OBJECT_MAP.put("TypecastParenPadCheck",
                csPackage + "checks.whitespace.TypecastParenPadCheck");
        OBJECT_MAP.put("TypecastParenPad",
                csPackage + "checks.whitespace.TypecastParenPadCheck");
        OBJECT_MAP.put("SimplifyBooleanExpressionCheck",
                csPackage + "checks.coding.SimplifyBooleanExpressionCheck");
        OBJECT_MAP.put("SimplifyBooleanExpression",
                csPackage + "checks.coding.SimplifyBooleanExpressionCheck");
        OBJECT_MAP.put("SuppressWarningsFilter",
                csPackage + "filters.SuppressWarningsFilter");
        OBJECT_MAP.put("ConstantNameCheck",
                csPackage + "checks.naming.ConstantNameCheck");
        OBJECT_MAP.put("ConstantName",
                csPackage + "checks.naming.ConstantNameCheck");
        OBJECT_MAP.put("AnonInnerLengthCheck",
                csPackage + "checks.sizes.AnonInnerLengthCheck");
        OBJECT_MAP.put("AnonInnerLength",
                csPackage + "checks.sizes.AnonInnerLengthCheck");
        OBJECT_MAP.put("HideUtilityClassConstructorCheck",
                csPackage + "checks.design.HideUtilityClassConstructorCheck");
        OBJECT_MAP.put("HideUtilityClassConstructor",
                csPackage + "checks.design.HideUtilityClassConstructorCheck");
        OBJECT_MAP.put("TypeNameCheck",
                csPackage + "checks.naming.TypeNameCheck");
        OBJECT_MAP.put("TypeName",
                csPackage + "checks.naming.TypeNameCheck");
        OBJECT_MAP.put("ImportOrderCheck",
                csPackage + "checks.imports.ImportOrderCheck");
        OBJECT_MAP.put("ImportOrder",
                csPackage + "checks.imports.ImportOrderCheck");
        OBJECT_MAP.put("DeclarationOrderCheck",
                csPackage + "checks.coding.DeclarationOrderCheck");
        OBJECT_MAP.put("DeclarationOrder",
                csPackage + "checks.coding.DeclarationOrderCheck");
        OBJECT_MAP.put("EmptyForInitializerPadCheck",
                csPackage + "checks.whitespace.EmptyForInitializerPadCheck");
        OBJECT_MAP.put("EmptyForInitializerPad",
                csPackage + "checks.whitespace.EmptyForInitializerPadCheck");
        OBJECT_MAP.put("NestedIfDepthCheck",
                csPackage + "checks.coding.NestedIfDepthCheck");
        OBJECT_MAP.put("NestedIfDepth",
                csPackage + "checks.coding.NestedIfDepthCheck");
    }

    /**
     * Creates a new {@code PackageObjectFactory} instance.
     * @param packageNames the list of package names to use
     * @param moduleClassLoader class loader used to load Checkstyle
     *          core and custom modules
     */
    public PackageObjectFactory(Set<String> packageNames, ClassLoader moduleClassLoader) {
        if (moduleClassLoader == null) {
            throw new IllegalArgumentException(
                    "moduleClassLoader must not be null");
        }

        //create a copy of the given set, but retain ordering
        packages = Sets.newLinkedHashSet(packageNames);
        this.moduleClassLoader = moduleClassLoader;
    }

    /**
     * Creates a new instance of a class from a given name, or that name
     * concatenated with &quot;Check&quot;. If the name is
     * a class name, creates an instance of the named class. Otherwise, creates
     * an instance of a class name obtained by concatenating the given name
     * to a package name from a given list of package names.
     * @param name the name of a class.
     * @return the {@code Object} created by loader.
     * @throws CheckstyleException if an error occurs.
     */
    @Override
    public Object createModule(String name) throws CheckstyleException {
        Object instance = createObjectFromMap(name);
        if (instance == null) {
            instance = createObjectFromMap(name + "Check");
            if (instance == null) {
                instance = createObjectWithIgnoringProblems(name, getAllPossibleNames(name));
                if (instance == null) {
                    final String nameCheck = name + "Check";
                    instance = createObjectWithIgnoringProblems(nameCheck, getAllPossibleNames(nameCheck));
                    if (instance == null) {
                        final String attemptedNames = joinPackageNamesWithClassName(name)
                                + STRING_SEPARATOR + nameCheck + STRING_SEPARATOR
                                + joinPackageNamesWithClassName(nameCheck);
                        final LocalizedMessage exceptionMessage = new LocalizedMessage(0,
                            Definitions.CHECKSTYLE_BUNDLE, UNABLE_TO_INSTANTIATE_EXCEPTION_MESSAGE,
                            new String[] {name, attemptedNames}, null, getClass(), null);
                        throw new CheckstyleException(exceptionMessage.getMessage());
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Creates a new instance of a class from CheckStyle's known classes.
     * @param className the name of the class to find and instantiate.
     * @return the {@code Object} created by loader or null.
     */
    private Object createObjectFromMap(String className) {
        final String fullPackage = OBJECT_MAP.get(className);
        final Object instance;

        if (fullPackage != null) {
            instance = createObject(className);
        }
        else {
            instance = null;
        }

        return instance;
    }

    /**
     * Create a new instance of a named class.
     * @param className the name of the class to instantiate.
     * @param secondAttempt the set of names to attempt instantiation
     *                      if usage of the className was not successful.
     * @return the {@code Object} created by loader or null.
     */
    private Object createObjectWithIgnoringProblems(String className,
                                                    Set<String> secondAttempt) {
        Object instance = createObject(className);
        if (instance == null) {
            final Iterator<String> ite = secondAttempt.iterator();
            while (instance == null && ite.hasNext()) {
                instance = createObject(ite.next());
            }
        }
        return instance;
    }

    /**
     * Generate the set of all possible names for a class name.
     * @param name the name of the class get possible names for.
     * @return all possible name for a class.
     */
    private Set<String> getAllPossibleNames(String name) {
        final Set<String> names = Sets.newHashSet();
        for (String packageName : packages) {
            names.add(packageName + name);
        }
        return names;
    }

    /**
     * Creates a string by joining package names with a class name.
     * @param className name of the class for joining.
     * @return a string which is obtained by joining package names with a class name.
     */
    private String joinPackageNamesWithClassName(String className) {
        final Joiner joiner = Joiner.on(className + STRING_SEPARATOR).skipNulls();
        return joiner.join(packages) + className;
    }

    /**
     * Creates a new instance of a named class.
     * @param className the name of the class to instantiate.
     * @return the {@code Object} created by loader or null.
     */
    private Object createObject(String className) {
        Object instance = null;
        try {
            final Class<?> clazz = Class.forName(className, true, moduleClassLoader);
            final Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            instance = declaredConstructor.newInstance();
        }
        catch (final ReflectiveOperationException | NoClassDefFoundError exception) {
            LOG.debug(IGNORING_EXCEPTION_MESSAGE, exception);
        }
        return instance;
    }

    public static Map<String, String> getObjectMap() {
        return OBJECT_MAP;
    }
}
