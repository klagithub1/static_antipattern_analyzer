package main.java.orm.detector.analyzer.bcelvisitor;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;


import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.LoopNestTree;

import main.java.orm.detector.logger.Logger;

import org.apache.bcel.generic.FieldGen;

public class ClassVisitor extends EmptyVisitor 
{
    private JavaClass clazz;
    private ConstantPoolGen constants;
    private int classId;
    
    public ClassVisitor(JavaClass jc, int classID) 
    {
    	classId = classID;
        clazz = jc;
        constants = new ConstantPoolGen(clazz.getConstantPool());
    }

    public void visitJavaClass(JavaClass jc) 
    {
    	//TODO get rid of this
        jc.getConstantPool().accept(this);
        System.out.println("INFO:Process all the Methods of the class");
        Logger.log("INFO:Process all the Methods of the class");
        Method[] methods = jc.getMethods();
        for (int i = 0; i < methods.length; i++)
        {
        	methods[i].accept(this);
        }  
        
        System.out.println("INFO:Process all the Attributes of the class");
        Logger.log("INFO:Process all the Attributes of the class");
        Field[] fields = jc.getFields();
        for(Field field: fields)
        {
        	field.accept(this);
        }  
    }

    //TODO get rid of this
    public void visitConstantPool(ConstantPool constantPool) 
    {
        for (int i = 0; i < constantPool.getLength(); i++) 
        {
            Constant constant = constantPool.getConstant(i);
            if (constant == null)
            {
            	continue;
            }
                
            if (constant.getTag() == 7) 
            {
                @SuppressWarnings("unused")
				String referencedClass = constantPool.constantToString(constant);
            }
        }
    }

    public void visitMethod(Method method)
    {
    	//System.out.println("visiting method: "+method.getName());
        MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
        MethodVisitor visitor = new MethodVisitor(mg, clazz);
        visitor.start(classId); 
        
        System.out.println("INFO: Processed method: "+method.getName());
        Logger.log("INFO: Processed method: "+method.getName());
    }
    
    public void visitField(Field attribute)
    {
    	//System.out.println("visiting attribute:  "+attribute.getName());
        FieldGen fg = new FieldGen(attribute, constants);
        FieldVisitor visitor = new FieldVisitor(fg, clazz);
        visitor.start(classId);
        
        //System.out.println("-- Processed attribute: "+attribute.getName());
        Logger.log("INFO: Processed attribute: "+attribute.getName());
    }

    public void start() 
    {
        visitJavaClass(clazz);
    }
}
