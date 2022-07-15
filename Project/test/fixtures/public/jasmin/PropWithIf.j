.class public PropWithIf
.super java/lang/Object

.method public foo(I)I
	.limit stack 3
	.limit locals 6
	iload_1
	istore_2
	bipush 10
	istore_3
	iload_2
	iload_3
	isub
	iflt ComparisonThen0
	iconst_0
	goto ComparisonEndIf0
	ComparisonThen0:
	iconst_1
	ComparisonEndIf0:
	istore 4
	iload 4
	ifne Then1
	iload 5
	istore 5
	goto Endif1
	Then1:
	iload_1
	istore 5
	Endif1:
	iload 5
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 0
	.limit locals 1
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

