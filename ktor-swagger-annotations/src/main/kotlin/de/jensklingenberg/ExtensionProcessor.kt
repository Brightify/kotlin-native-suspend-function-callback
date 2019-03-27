package de.jensklingenberg

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import de.jensklingenberg.compiler.common.methods
import de.jensklingenberg.compiler.common.simpleName
import de.jensklingenberg.compiler.kaptmpp.*
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.File

class ExtensionProcessor(configuration: CompilerConfiguration) :
        AbstractProcessor(configuration) {


    override fun getSupportedPlatform() = listOf(Platform.ALL)

    val TAG = "ExtensionProcessor:"


    override fun process(roundEnvironment: RoundEnvironment): Boolean {


        roundEnvironment
                .getElementsAnnotatedWith(listOf("annotation.Module","sample.FunExt","sample.Hello"))
                .forEach { element ->


                    configuration.kotlinSourceRoots.forEach {
                        processingEnv.messager.report(
                                CompilerMessageSeverity.WARNING, ""
                                //      TAG + "***SourceRott " + it.path + " " + roundEnvironment.platform.name
                        )
                    }


                    when (element) {

                        is Element.ClassElement -> {

                            val targetClass = listOf(element.descriptor)
                          //  val targetClass = (element.annotation?.readArgument("to")?.value as ArrayList<KClassValue>).map { it.getArgumentType(element.descriptor.module)?.constructor?.declarationDescriptor as? ClassDescriptor }.filterNotNull()

                            targetClass.forEach {
                                processingEnv.messager.report(
                                        CompilerMessageSeverity.WARNING,
                                        TAG + "***Class " + element.descriptor.name + " annoated with: "+element.annotation.simpleName()+ " " + roundEnvironment.module?.name + " with target class :"+it.name
                                )

                                it.methods().forEach {method->
                                    processingEnv.messager.report(
                                            CompilerMessageSeverity.WARNING,
                                            TAG + "***Method " + method.name
                                    )
                                }

                            }




                        }
                    }

                    processingEnv.messager.report(
                            CompilerMessageSeverity.WARNING, ""
                            //TAG + "*** process ***" + it.simpleName + "\n Pack" + it.pack + "\n Annotation:" + it.annotation + "\n buildfolder" + processingEnv.buildFolder+ "\nplatform: "+roundEnvironment.platform.name
                    )




                }




        roundEnvironment
                .getElementsAnnotatedWith("annotation.Module")
                .forEach {
                    val es = it
                    when (es) {


                        is Element.FunctionElement -> {
                            processingEnv.messager.report(
                                    CompilerMessageSeverity.WARNING,
                                    TAG + "***Function-------> " + es.simpleName +  " annotated with " + es.annotation.simpleName()
                            )
                        }
                    }
                }
        return true
    }


    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()

    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
                "de.jensklingenberg.annotation.Extension","sample.FunExt","annotation.Module","sample.Hello"
        )

    }


    override fun initProcessor() {
        processingEnv.messager.report(
                CompilerMessageSeverity.WARNING,
                //  "*** IT'S ENTER FUNCTION ***"+function.elements.hasAnnotation(FqName("me.eugeniomarletti.Hallo")))
                TAG + "***Processor started ***"
        )

    }

    override fun processingOver(){

        File(processingEnv.projectFolder + "generated/jvm/kotlin").deleteRecursively()

        processingEnv.messager.report(
                CompilerMessageSeverity.WARNING,
                //  "*** IT'S ENTER FUNCTION ***"+function.elements.hasAnnotation(FqName("me.eugeniomarletti.Hallo")))
                TAG + "***Processor over ***"
        )

        val greeterClass = ClassName("", "Greeter")
        val file = FileSpec.builder("de.jensklingenberg", "DaggerAppComponent")
                .addComment("//Generated")
                .addType(TypeSpec.classBuilder("DaggerAppComponent").addSuperinterface(ClassName("sample", "AppComponent"))
                        .addFunction(FunSpec.builder("greet")
                                .addStatement("println(%P)", "Hello, \$name")
                                .build())
                        .build())

                .build()
        //file.writeTo(File(processingEnv.projectFolder + "generated/linuxMain/kotlin"))
    }


}