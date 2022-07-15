.class public FuncArgsFuncCall
.super java/lang/Object

.method public f1(I)I
	.limit stack 1
	.limit locals 2
	iload_1
	ireturn
.end method

.method public f2(I)I
	.limit stack 1
	.limit locals 2
	iload_1
	ireturn
.end method

.method public func(II)I
	.limit stack 1
	.limit locals 5
	iload_1
	istore_3
	iload_3
	invokestatic ioPlus/printResult(I)V
	iload_2
	istore 4
	iload 4
	invokestatic ioPlus/printResult(I)V
	iconst_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 9
	new FuncArgsFuncCall
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial FuncArgsFuncCall/<init>()V
	bipush 10
	istore_3
	iconst_5
	istore 4
	iload_3
	istore 5
	aload_2
	iload 5
	invokevirtual FuncArgsFuncCall/f1(I)I
	istore 6
	iload 4
	istore 7
	aload_2
	iload 7
	invokevirtual FuncArgsFuncCall/f2(I)I
	istore 8
	aload_2
	iload 6
	iload 8
	invokevirtual FuncArgsFuncCall/func(II)I
	istore_3
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

