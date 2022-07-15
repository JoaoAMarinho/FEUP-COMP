.class public SimpleIfElseNot
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 1
	.limit locals 5
	iconst_1
	ifne Then1
	bipush 20
	istore_1
	iload_1
	invokestatic io/println(I)V
	goto Endif1
	Then1:
	bipush 10
	istore_2
	iload_2
	invokestatic io/println(I)V
	Endif1:
	iconst_0
	ifne Then2
	sipush 200
	istore_3
	iload_3
	invokestatic io/print(I)V
	goto Endif2
	Then2:
	bipush 100
	istore 4
	iload 4
	invokestatic io/print(I)V
	Endif2:
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

