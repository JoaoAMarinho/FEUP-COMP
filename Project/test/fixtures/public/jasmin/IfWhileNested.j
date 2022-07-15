.class public IfWhileNested
.super java/lang/Object

.method public func(I)I
	.limit stack 3
	.limit locals 12
	iconst_1
	istore_2
	iconst_0
	istore_3
	Loop1:
	iload_3
	istore 4
	iload_1
	istore 5
	iload 4
	iload 5
	isub
	iflt ComparisonThen0
	iconst_0
	goto ComparisonEndIf0
	ComparisonThen0:
	iconst_1
	ComparisonEndIf0:
	istore 6
	iload 6
	ifne Body1
	goto EndLoop1
	Body1:
	iload_2
	ifne Then2
	iconst_2
	istore 7
	iload 7
	invokestatic ioPlus/printResult(I)V
	goto Endif2
	Then2:
	iconst_1
	istore 8
	iload 8
	invokestatic ioPlus/printResult(I)V
	Endif2:
	iload_2
	ifne Then1
	iconst_1
	goto EndIf1
	Then1:
	iconst_0
	EndIf1:
	istore_2
	iload_3
	istore 9
	iconst_1
	istore 10
	iload 9
	iload 10
	iadd
	istore 11
	iload 11
	istore_3
	goto Loop1
	EndLoop1:
	iconst_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 5
	new IfWhileNested
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial IfWhileNested/<init>()V
	iconst_3
	istore_3
	aload_2
	iload_3
	invokevirtual IfWhileNested/func(I)I
	istore 4
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

