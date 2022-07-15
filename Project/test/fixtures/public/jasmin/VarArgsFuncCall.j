.class public VarArgsFuncCall
.super java/lang/Object

.method public func(III)I
	.limit stack 1
	.limit locals 7
	iload_1
	istore 4
	iload 4
	invokestatic ioPlus/printResult(I)V
	iload_2
	istore 5
	iload 5
	invokestatic ioPlus/printResult(I)V
	iload_3
	istore 6
	iload 6
	invokestatic ioPlus/printResult(I)V
	iconst_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 9
	new VarArgsFuncCall
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial VarArgsFuncCall/<init>()V
	bipush 10
	istore_3
	bipush 12
	istore 4
	bipush 11
	istore 5
	iload_3
	istore 6
	iload 4
	istore 7
	iload 5
	istore 8
	aload_2
	iload 6
	iload 7
	iload 8
	invokevirtual VarArgsFuncCall/func(III)I
	istore_3
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

