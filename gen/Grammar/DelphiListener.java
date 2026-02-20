// Generated from /home/rrao/IdeaProjects/PLP_PROJECT_P1/src/Grammar/Delphi.g4 by ANTLR 4.13.2
package Grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DelphiParser}.
 */
public interface DelphiListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DelphiParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(DelphiParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(DelphiParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#programHeading}.
	 * @param ctx the parse tree
	 */
	void enterProgramHeading(DelphiParser.ProgramHeadingContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#programHeading}.
	 * @param ctx the parse tree
	 */
	void exitProgramHeading(DelphiParser.ProgramHeadingContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(DelphiParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(DelphiParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#topLevelBlock}.
	 * @param ctx the parse tree
	 */
	void enterTopLevelBlock(DelphiParser.TopLevelBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#topLevelBlock}.
	 * @param ctx the parse tree
	 */
	void exitTopLevelBlock(DelphiParser.TopLevelBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#expandedTypeDefinitionPart}.
	 * @param ctx the parse tree
	 */
	void enterExpandedTypeDefinitionPart(DelphiParser.ExpandedTypeDefinitionPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#expandedTypeDefinitionPart}.
	 * @param ctx the parse tree
	 */
	void exitExpandedTypeDefinitionPart(DelphiParser.ExpandedTypeDefinitionPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#expandedTypeDefinition}.
	 * @param ctx the parse tree
	 */
	void enterExpandedTypeDefinition(DelphiParser.ExpandedTypeDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#expandedTypeDefinition}.
	 * @param ctx the parse tree
	 */
	void exitExpandedTypeDefinition(DelphiParser.ExpandedTypeDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(DelphiParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(DelphiParser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#anscestor}.
	 * @param ctx the parse tree
	 */
	void enterAnscestor(DelphiParser.AnscestorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#anscestor}.
	 * @param ctx the parse tree
	 */
	void exitAnscestor(DelphiParser.AnscestorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#classDefinition}.
	 * @param ctx the parse tree
	 */
	void enterClassDefinition(DelphiParser.ClassDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#classDefinition}.
	 * @param ctx the parse tree
	 */
	void exitClassDefinition(DelphiParser.ClassDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#primaryFieldDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryFieldDeclarationPart(DelphiParser.PrimaryFieldDeclarationPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#primaryFieldDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryFieldDeclarationPart(DelphiParser.PrimaryFieldDeclarationPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#memberListPart}.
	 * @param ctx the parse tree
	 */
	void enterMemberListPart(DelphiParser.MemberListPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#memberListPart}.
	 * @param ctx the parse tree
	 */
	void exitMemberListPart(DelphiParser.MemberListPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#accessSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterAccessSpecifier(DelphiParser.AccessSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#accessSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitAccessSpecifier(DelphiParser.AccessSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#methodPrototype}.
	 * @param ctx the parse tree
	 */
	void enterMethodPrototype(DelphiParser.MethodPrototypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#methodPrototype}.
	 * @param ctx the parse tree
	 */
	void exitMethodPrototype(DelphiParser.MethodPrototypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#procedurePrototype}.
	 * @param ctx the parse tree
	 */
	void enterProcedurePrototype(DelphiParser.ProcedurePrototypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#procedurePrototype}.
	 * @param ctx the parse tree
	 */
	void exitProcedurePrototype(DelphiParser.ProcedurePrototypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#constructorPrototype}.
	 * @param ctx the parse tree
	 */
	void enterConstructorPrototype(DelphiParser.ConstructorPrototypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#constructorPrototype}.
	 * @param ctx the parse tree
	 */
	void exitConstructorPrototype(DelphiParser.ConstructorPrototypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#destructorPrototype}.
	 * @param ctx the parse tree
	 */
	void enterDestructorPrototype(DelphiParser.DestructorPrototypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#destructorPrototype}.
	 * @param ctx the parse tree
	 */
	void exitDestructorPrototype(DelphiParser.DestructorPrototypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#functionPrototype}.
	 * @param ctx the parse tree
	 */
	void enterFunctionPrototype(DelphiParser.FunctionPrototypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#functionPrototype}.
	 * @param ctx the parse tree
	 */
	void exitFunctionPrototype(DelphiParser.FunctionPrototypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#classFieldDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void enterClassFieldDeclarationPart(DelphiParser.ClassFieldDeclarationPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#classFieldDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void exitClassFieldDeclarationPart(DelphiParser.ClassFieldDeclarationPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#interfaceType}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceType(DelphiParser.InterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#interfaceType}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceType(DelphiParser.InterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#interfaceGuid}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceGuid(DelphiParser.InterfaceGuidContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#interfaceGuid}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceGuid(DelphiParser.InterfaceGuidContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#usesUnitsPart}.
	 * @param ctx the parse tree
	 */
	void enterUsesUnitsPart(DelphiParser.UsesUnitsPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#usesUnitsPart}.
	 * @param ctx the parse tree
	 */
	void exitUsesUnitsPart(DelphiParser.UsesUnitsPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#labelDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void enterLabelDeclarationPart(DelphiParser.LabelDeclarationPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#labelDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void exitLabelDeclarationPart(DelphiParser.LabelDeclarationPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(DelphiParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(DelphiParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#constantDefinitionPart}.
	 * @param ctx the parse tree
	 */
	void enterConstantDefinitionPart(DelphiParser.ConstantDefinitionPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#constantDefinitionPart}.
	 * @param ctx the parse tree
	 */
	void exitConstantDefinitionPart(DelphiParser.ConstantDefinitionPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#constantDefinition}.
	 * @param ctx the parse tree
	 */
	void enterConstantDefinition(DelphiParser.ConstantDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#constantDefinition}.
	 * @param ctx the parse tree
	 */
	void exitConstantDefinition(DelphiParser.ConstantDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#constantChr}.
	 * @param ctx the parse tree
	 */
	void enterConstantChr(DelphiParser.ConstantChrContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#constantChr}.
	 * @param ctx the parse tree
	 */
	void exitConstantChr(DelphiParser.ConstantChrContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(DelphiParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(DelphiParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#unsignedNumber}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedNumber(DelphiParser.UnsignedNumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#unsignedNumber}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedNumber(DelphiParser.UnsignedNumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#unsignedInteger}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedInteger(DelphiParser.UnsignedIntegerContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#unsignedInteger}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedInteger(DelphiParser.UnsignedIntegerContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#unsignedReal}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedReal(DelphiParser.UnsignedRealContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#unsignedReal}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedReal(DelphiParser.UnsignedRealContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#sign}.
	 * @param ctx the parse tree
	 */
	void enterSign(DelphiParser.SignContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#sign}.
	 * @param ctx the parse tree
	 */
	void exitSign(DelphiParser.SignContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#bool_}.
	 * @param ctx the parse tree
	 */
	void enterBool_(DelphiParser.Bool_Context ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#bool_}.
	 * @param ctx the parse tree
	 */
	void exitBool_(DelphiParser.Bool_Context ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(DelphiParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(DelphiParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#typeDefinitionPart}.
	 * @param ctx the parse tree
	 */
	void enterTypeDefinitionPart(DelphiParser.TypeDefinitionPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#typeDefinitionPart}.
	 * @param ctx the parse tree
	 */
	void exitTypeDefinitionPart(DelphiParser.TypeDefinitionPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#typeDefinition}.
	 * @param ctx the parse tree
	 */
	void enterTypeDefinition(DelphiParser.TypeDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#typeDefinition}.
	 * @param ctx the parse tree
	 */
	void exitTypeDefinition(DelphiParser.TypeDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#functionType}.
	 * @param ctx the parse tree
	 */
	void enterFunctionType(DelphiParser.FunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#functionType}.
	 * @param ctx the parse tree
	 */
	void exitFunctionType(DelphiParser.FunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#procedureType}.
	 * @param ctx the parse tree
	 */
	void enterProcedureType(DelphiParser.ProcedureTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#procedureType}.
	 * @param ctx the parse tree
	 */
	void exitProcedureType(DelphiParser.ProcedureTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#type_}.
	 * @param ctx the parse tree
	 */
	void enterType_(DelphiParser.Type_Context ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#type_}.
	 * @param ctx the parse tree
	 */
	void exitType_(DelphiParser.Type_Context ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#simpleType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleType(DelphiParser.SimpleTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#simpleType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleType(DelphiParser.SimpleTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#scalarType}.
	 * @param ctx the parse tree
	 */
	void enterScalarType(DelphiParser.ScalarTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#scalarType}.
	 * @param ctx the parse tree
	 */
	void exitScalarType(DelphiParser.ScalarTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#subrangeType}.
	 * @param ctx the parse tree
	 */
	void enterSubrangeType(DelphiParser.SubrangeTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#subrangeType}.
	 * @param ctx the parse tree
	 */
	void exitSubrangeType(DelphiParser.SubrangeTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#typeIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterTypeIdentifier(DelphiParser.TypeIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#typeIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitTypeIdentifier(DelphiParser.TypeIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#structuredType}.
	 * @param ctx the parse tree
	 */
	void enterStructuredType(DelphiParser.StructuredTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#structuredType}.
	 * @param ctx the parse tree
	 */
	void exitStructuredType(DelphiParser.StructuredTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#unpackedStructuredType}.
	 * @param ctx the parse tree
	 */
	void enterUnpackedStructuredType(DelphiParser.UnpackedStructuredTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#unpackedStructuredType}.
	 * @param ctx the parse tree
	 */
	void exitUnpackedStructuredType(DelphiParser.UnpackedStructuredTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#stringtype}.
	 * @param ctx the parse tree
	 */
	void enterStringtype(DelphiParser.StringtypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#stringtype}.
	 * @param ctx the parse tree
	 */
	void exitStringtype(DelphiParser.StringtypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(DelphiParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(DelphiParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(DelphiParser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(DelphiParser.TypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#indexType}.
	 * @param ctx the parse tree
	 */
	void enterIndexType(DelphiParser.IndexTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#indexType}.
	 * @param ctx the parse tree
	 */
	void exitIndexType(DelphiParser.IndexTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#componentType}.
	 * @param ctx the parse tree
	 */
	void enterComponentType(DelphiParser.ComponentTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#componentType}.
	 * @param ctx the parse tree
	 */
	void exitComponentType(DelphiParser.ComponentTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#recordType}.
	 * @param ctx the parse tree
	 */
	void enterRecordType(DelphiParser.RecordTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#recordType}.
	 * @param ctx the parse tree
	 */
	void exitRecordType(DelphiParser.RecordTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#fieldList}.
	 * @param ctx the parse tree
	 */
	void enterFieldList(DelphiParser.FieldListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#fieldList}.
	 * @param ctx the parse tree
	 */
	void exitFieldList(DelphiParser.FieldListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#fixedPart}.
	 * @param ctx the parse tree
	 */
	void enterFixedPart(DelphiParser.FixedPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#fixedPart}.
	 * @param ctx the parse tree
	 */
	void exitFixedPart(DelphiParser.FixedPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#recordSection}.
	 * @param ctx the parse tree
	 */
	void enterRecordSection(DelphiParser.RecordSectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#recordSection}.
	 * @param ctx the parse tree
	 */
	void exitRecordSection(DelphiParser.RecordSectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#variantPart}.
	 * @param ctx the parse tree
	 */
	void enterVariantPart(DelphiParser.VariantPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#variantPart}.
	 * @param ctx the parse tree
	 */
	void exitVariantPart(DelphiParser.VariantPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#tag}.
	 * @param ctx the parse tree
	 */
	void enterTag(DelphiParser.TagContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#tag}.
	 * @param ctx the parse tree
	 */
	void exitTag(DelphiParser.TagContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#variant}.
	 * @param ctx the parse tree
	 */
	void enterVariant(DelphiParser.VariantContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#variant}.
	 * @param ctx the parse tree
	 */
	void exitVariant(DelphiParser.VariantContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#setType}.
	 * @param ctx the parse tree
	 */
	void enterSetType(DelphiParser.SetTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#setType}.
	 * @param ctx the parse tree
	 */
	void exitSetType(DelphiParser.SetTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#baseType}.
	 * @param ctx the parse tree
	 */
	void enterBaseType(DelphiParser.BaseTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#baseType}.
	 * @param ctx the parse tree
	 */
	void exitBaseType(DelphiParser.BaseTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#fileType}.
	 * @param ctx the parse tree
	 */
	void enterFileType(DelphiParser.FileTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#fileType}.
	 * @param ctx the parse tree
	 */
	void exitFileType(DelphiParser.FileTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#pointerType}.
	 * @param ctx the parse tree
	 */
	void enterPointerType(DelphiParser.PointerTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#pointerType}.
	 * @param ctx the parse tree
	 */
	void exitPointerType(DelphiParser.PointerTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#variableDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarationPart(DelphiParser.VariableDeclarationPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#variableDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarationPart(DelphiParser.VariableDeclarationPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(DelphiParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(DelphiParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#procedureAndFunctionDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void enterProcedureAndFunctionDeclarationPart(DelphiParser.ProcedureAndFunctionDeclarationPartContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#procedureAndFunctionDeclarationPart}.
	 * @param ctx the parse tree
	 */
	void exitProcedureAndFunctionDeclarationPart(DelphiParser.ProcedureAndFunctionDeclarationPartContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#procedureOrFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterProcedureOrFunctionDeclaration(DelphiParser.ProcedureOrFunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#procedureOrFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitProcedureOrFunctionDeclaration(DelphiParser.ProcedureOrFunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(DelphiParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(DelphiParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#procedureDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterProcedureDeclaration(DelphiParser.ProcedureDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#procedureDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitProcedureDeclaration(DelphiParser.ProcedureDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(DelphiParser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(DelphiParser.MethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(DelphiParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(DelphiParser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#destructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterDestructorDeclaration(DelphiParser.DestructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#destructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitDestructorDeclaration(DelphiParser.DestructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(DelphiParser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(DelphiParser.FormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#formalParameterSection}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterSection(DelphiParser.FormalParameterSectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#formalParameterSection}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterSection(DelphiParser.FormalParameterSectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#parameterGroup}.
	 * @param ctx the parse tree
	 */
	void enterParameterGroup(DelphiParser.ParameterGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#parameterGroup}.
	 * @param ctx the parse tree
	 */
	void exitParameterGroup(DelphiParser.ParameterGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierList(DelphiParser.IdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierList(DelphiParser.IdentifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#constList}.
	 * @param ctx the parse tree
	 */
	void enterConstList(DelphiParser.ConstListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#constList}.
	 * @param ctx the parse tree
	 */
	void exitConstList(DelphiParser.ConstListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(DelphiParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(DelphiParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#resultType}.
	 * @param ctx the parse tree
	 */
	void enterResultType(DelphiParser.ResultTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#resultType}.
	 * @param ctx the parse tree
	 */
	void exitResultType(DelphiParser.ResultTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(DelphiParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(DelphiParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#unlabelledStatement}.
	 * @param ctx the parse tree
	 */
	void enterUnlabelledStatement(DelphiParser.UnlabelledStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#unlabelledStatement}.
	 * @param ctx the parse tree
	 */
	void exitUnlabelledStatement(DelphiParser.UnlabelledStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void enterSimpleStatement(DelphiParser.SimpleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void exitSimpleStatement(DelphiParser.SimpleStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#assignmentStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentStatement(DelphiParser.AssignmentStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#assignmentStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentStatement(DelphiParser.AssignmentStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(DelphiParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(DelphiParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(DelphiParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(DelphiParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#relationaloperator}.
	 * @param ctx the parse tree
	 */
	void enterRelationaloperator(DelphiParser.RelationaloperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#relationaloperator}.
	 * @param ctx the parse tree
	 */
	void exitRelationaloperator(DelphiParser.RelationaloperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpression(DelphiParser.SimpleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpression(DelphiParser.SimpleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#additiveoperator}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveoperator(DelphiParser.AdditiveoperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#additiveoperator}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveoperator(DelphiParser.AdditiveoperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(DelphiParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(DelphiParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#multiplicativeoperator}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeoperator(DelphiParser.MultiplicativeoperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#multiplicativeoperator}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeoperator(DelphiParser.MultiplicativeoperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#signedFactor}.
	 * @param ctx the parse tree
	 */
	void enterSignedFactor(DelphiParser.SignedFactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#signedFactor}.
	 * @param ctx the parse tree
	 */
	void exitSignedFactor(DelphiParser.SignedFactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(DelphiParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(DelphiParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#unsignedConstant}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedConstant(DelphiParser.UnsignedConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#unsignedConstant}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedConstant(DelphiParser.UnsignedConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#functionDesignator}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDesignator(DelphiParser.FunctionDesignatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#functionDesignator}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDesignator(DelphiParser.FunctionDesignatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(DelphiParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(DelphiParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#set_}.
	 * @param ctx the parse tree
	 */
	void enterSet_(DelphiParser.Set_Context ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#set_}.
	 * @param ctx the parse tree
	 */
	void exitSet_(DelphiParser.Set_Context ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#elementList}.
	 * @param ctx the parse tree
	 */
	void enterElementList(DelphiParser.ElementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#elementList}.
	 * @param ctx the parse tree
	 */
	void exitElementList(DelphiParser.ElementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(DelphiParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(DelphiParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#procedureStatement}.
	 * @param ctx the parse tree
	 */
	void enterProcedureStatement(DelphiParser.ProcedureStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#procedureStatement}.
	 * @param ctx the parse tree
	 */
	void exitProcedureStatement(DelphiParser.ProcedureStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#actualParameter}.
	 * @param ctx the parse tree
	 */
	void enterActualParameter(DelphiParser.ActualParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#actualParameter}.
	 * @param ctx the parse tree
	 */
	void exitActualParameter(DelphiParser.ActualParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#parameterwidth}.
	 * @param ctx the parse tree
	 */
	void enterParameterwidth(DelphiParser.ParameterwidthContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#parameterwidth}.
	 * @param ctx the parse tree
	 */
	void exitParameterwidth(DelphiParser.ParameterwidthContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#gotoStatement}.
	 * @param ctx the parse tree
	 */
	void enterGotoStatement(DelphiParser.GotoStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#gotoStatement}.
	 * @param ctx the parse tree
	 */
	void exitGotoStatement(DelphiParser.GotoStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#emptyStatement_}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStatement_(DelphiParser.EmptyStatement_Context ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#emptyStatement_}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStatement_(DelphiParser.EmptyStatement_Context ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#empty_}.
	 * @param ctx the parse tree
	 */
	void enterEmpty_(DelphiParser.Empty_Context ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#empty_}.
	 * @param ctx the parse tree
	 */
	void exitEmpty_(DelphiParser.Empty_Context ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#structuredStatement}.
	 * @param ctx the parse tree
	 */
	void enterStructuredStatement(DelphiParser.StructuredStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#structuredStatement}.
	 * @param ctx the parse tree
	 */
	void exitStructuredStatement(DelphiParser.StructuredStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(DelphiParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(DelphiParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(DelphiParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(DelphiParser.StatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#conditionalStatement}.
	 * @param ctx the parse tree
	 */
	void enterConditionalStatement(DelphiParser.ConditionalStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#conditionalStatement}.
	 * @param ctx the parse tree
	 */
	void exitConditionalStatement(DelphiParser.ConditionalStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(DelphiParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(DelphiParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#caseStatement}.
	 * @param ctx the parse tree
	 */
	void enterCaseStatement(DelphiParser.CaseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#caseStatement}.
	 * @param ctx the parse tree
	 */
	void exitCaseStatement(DelphiParser.CaseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#caseListElement}.
	 * @param ctx the parse tree
	 */
	void enterCaseListElement(DelphiParser.CaseListElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#caseListElement}.
	 * @param ctx the parse tree
	 */
	void exitCaseListElement(DelphiParser.CaseListElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#repetetiveStatement}.
	 * @param ctx the parse tree
	 */
	void enterRepetetiveStatement(DelphiParser.RepetetiveStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#repetetiveStatement}.
	 * @param ctx the parse tree
	 */
	void exitRepetetiveStatement(DelphiParser.RepetetiveStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(DelphiParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(DelphiParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#repeatStatement}.
	 * @param ctx the parse tree
	 */
	void enterRepeatStatement(DelphiParser.RepeatStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#repeatStatement}.
	 * @param ctx the parse tree
	 */
	void exitRepeatStatement(DelphiParser.RepeatStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(DelphiParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(DelphiParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#forList}.
	 * @param ctx the parse tree
	 */
	void enterForList(DelphiParser.ForListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#forList}.
	 * @param ctx the parse tree
	 */
	void exitForList(DelphiParser.ForListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#initialValue}.
	 * @param ctx the parse tree
	 */
	void enterInitialValue(DelphiParser.InitialValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#initialValue}.
	 * @param ctx the parse tree
	 */
	void exitInitialValue(DelphiParser.InitialValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#finalValue}.
	 * @param ctx the parse tree
	 */
	void enterFinalValue(DelphiParser.FinalValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#finalValue}.
	 * @param ctx the parse tree
	 */
	void exitFinalValue(DelphiParser.FinalValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#withStatement}.
	 * @param ctx the parse tree
	 */
	void enterWithStatement(DelphiParser.WithStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#withStatement}.
	 * @param ctx the parse tree
	 */
	void exitWithStatement(DelphiParser.WithStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#recordVariableList}.
	 * @param ctx the parse tree
	 */
	void enterRecordVariableList(DelphiParser.RecordVariableListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#recordVariableList}.
	 * @param ctx the parse tree
	 */
	void exitRecordVariableList(DelphiParser.RecordVariableListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(DelphiParser.MethodCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(DelphiParser.MethodCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link DelphiParser#access}.
	 * @param ctx the parse tree
	 */
	void enterAccess(DelphiParser.AccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link DelphiParser#access}.
	 * @param ctx the parse tree
	 */
	void exitAccess(DelphiParser.AccessContext ctx);
}