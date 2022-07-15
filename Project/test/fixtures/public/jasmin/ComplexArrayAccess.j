.class public ComplexArrayAccess
.super java/lang/Object

.method public func(I)I
	.limit stack 1
	.limit locals 2
	iload_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 42
	new ComplexArrayAccess
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial ComplexArrayAccess/<init>()V
	iconst_5
	istore_3
	iload_3
	newarray int
	astore 4
	iconst_0
	istore 5
	aload 4
	iload 5
	iconst_1
	iastore
	iconst_1
	istore 6
	aload 4
	iload 6
	iconst_2
	iastore
	iconst_2
	istore 7
	aload 4
	iload 7
	iconst_3
	iastore
	iconst_3
	istore 8
	aload 4
	iload 8
	iconst_4
	iastore
	iconst_4
	istore 9
	aload 4
	iload 9
	iconst_5
	iastore
	iconst_1
	istore 10
	iconst_0
	istore 11
	aload_2
	iload 11
	invokevirtual ComplexArrayAccess/func(I)I
	istore 12
	aload 4
	astore 13
	aload 13
	iload 12
	iaload
	istore 14
	iload 14
	istore 15
	iload 15
	invokestatic ioPlus/printResult(I)V
	iload 10
	istore 16
	aload 4
	astore 17
	aload 17
	iload 16
	iaload
	istore 18
	iload 18
	istore 19
	iload 19
	invokestatic ioPlus/printResult(I)V
	iconst_4
	istore 20
	iconst_2
	istore 21
	iload 20
	iload 21
	idiv
	istore 22
	iload 22
	istore 23
	aload 4
	astore 24
	aload 24
	iload 23
	iaload
	istore 25
	iload 25
	istore 26
	iload 26
	invokestatic ioPlus/printResult(I)V
	iconst_2
	istore 27
	aload 4
	astore 28
	aload 28
	iload 27
	iaload
	istore 29
	iload 29
	istore 30
	aload 4
	astore 31
	aload 31
	iload 30
	iaload
	istore 32
	iload 32
	istore 33
	iload 33
	invokestatic ioPlus/printResult(I)V
	aload 4
	astore 34
	aload 34
	arraylength
	istore 35
	iconst_1
	istore 36
	iload 35
	iload 36
	isub
	istore 37
	iload 37
	istore 38
	aload 4
	astore 39
	aload 39
	iload 38
	iaload
	istore 40
	iload 40
	istore 41
	iload 41
	invokestatic ioPlus/printResult(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

