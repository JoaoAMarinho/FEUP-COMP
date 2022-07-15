.class public ArrayAsArg
.super java/lang/Object

.method public func([I)I
	.limit stack 2
	.limit locals 8
	new ArrayAsArg
	astore_2
	aload_2
	astore_3
	aload_3
	invokespecial ArrayAsArg/<init>()V
	iconst_2
	istore 4
	iload 4
	newarray int
	astore 5
	aload 5
	astore 6
	aload_3
	aload 6
	invokevirtual ArrayAsArg/func([I)I
	istore 7
	iload 7
	ireturn
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

