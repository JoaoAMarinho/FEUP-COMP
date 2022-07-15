.class public ArrayInit
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 1
	.limit locals 5
	iconst_5
	istore_1
	iload_1
	newarray int
	astore_2
	aload_2
	astore_3
	aload_3
	arraylength
	istore 4
	iload 4
	invokestatic ioPlus/printResult(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

