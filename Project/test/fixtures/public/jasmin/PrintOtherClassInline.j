.class public PrintOtherClassInline
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 6
	new GetterAndSetter
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial GetterAndSetter/<init>()V
	bipush 10
	istore_3
	aload_2
	iload_3
	invokevirtual GetterAndSetter/setA(I)I
	istore 4
	aload_2
	invokevirtual GetterAndSetter/getA()I
	istore 4
	iload 4
	istore 5
	iload 5
	invokestatic io/print(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

