package ast.codegen;

import ast.TranslationUnitNode;

public class CodeGenerator {
    private final CodegenOptions options;

    public CodeGenerator(CodegenOptions options) {
        this.options = options;
    }

    public String generate(TranslationUnitNode tu) {
        CodegenContext ctx = new CodegenContext(options);

        tu.discover(ctx);
        ctx.out.writeln("import typing\n");

        tu.toPython(0, ctx);

        if (options.emitEntryPoint && ctx.meta.hasMain) {
            ctx.out.writeln("");
            ctx.out.writeln("if __name__ == \"__main__\":");
            ctx.out.indent();
            ctx.out.writeln("main()");
            ctx.out.dedent();
        }

        return ctx.out.toString();
    }
}
