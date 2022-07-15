.class public InvokeStatic
.super java/lang/Object

.method public func()I
	.limit stack 1
	.limit locals 2
	iconst_1
	istore_1
	iload_1
	invokestatic ioPlus/printResult(I)V
	iconst_1
	ireturn
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

