package ast.codegen;

public final class CodegenOptions {
    public final boolean emitEntryPoint;
    public final boolean blankLineBetweenDecls;
    public final boolean keepCppComments;

    private CodegenOptions(boolean entry, boolean blank, boolean keep) {
        this.emitEntryPoint = entry;
        this.blankLineBetweenDecls = blank;
        this.keepCppComments = keep;
    }

    public static CodegenOptions defaults() {
        return new CodegenOptions(true, true, false);
    }

    public CodegenOptions withEmitEntryPoint(boolean v)       { return new CodegenOptions(v,   blankLineBetweenDecls, keepCppComments); }
    public CodegenOptions withBlankLineBetweenDecls(boolean v){ return new CodegenOptions(emitEntryPoint, v,         keepCppComments); }
}
