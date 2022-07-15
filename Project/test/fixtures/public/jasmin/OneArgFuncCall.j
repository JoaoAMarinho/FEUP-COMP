.class public OneArgFuncCall
.super java/lang/Object

.method public func(I)I
	.limit stack 1
	.limit locals 2
	iload_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 6
	new OneArgFuncCall
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial OneArgFuncCall/<init>()V
	bipush 10
	istore_3
	aload_2
	iload_3
	invokevirtual OneArgFuncCall/func(I)I
	istore 4
	iload 4
	istore 5
	iload 5
	invokestatic ioPlus/printResult(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

