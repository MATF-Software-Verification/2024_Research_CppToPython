package ast;

import ast.codegen.CodegenContext;
import ast.codegen.CodegenOptions;

public class CodeGenerator {
    private final CodegenOptions options;

    public CodeGenerator(CodegenOptions options) {
        this.options = options;
    }

    public String generate(TranslationUnitNode tu) {
        CodegenContext ctx = new CodegenContext(options);

        tu.discover(ctx);
        tu.collectImports(ctx);
        ctx.out.write(ctx.emitImports());

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
