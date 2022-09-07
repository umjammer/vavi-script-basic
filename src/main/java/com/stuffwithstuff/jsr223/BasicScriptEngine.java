/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.stuffwithstuff.jsr223;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import com.stuffwithstuff.Jasic;
import net.sf.cocoa.basic.CommandInterpreter;


/**
 * BasicScriptEngine.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 220726 nsano make the initial version <br>
 */
public class BasicScriptEngine implements ScriptEngine {

    /** */
    private static final String __ENGINE_VERSION__ = "1.0";
    /** */
    private static final String MY_NAME = "JASIC";
    /** */
    private static final String MY_SHORT_NAME = "jasic";
    /** */
    private static final String STR_THISLANGUAGE = "Basic";

    /** */
    private ScriptEngineFactory factory;

    /** */
    private ScriptContext defaultContext;

    /** */
    public BasicScriptEngine(BasicScriptEngineFactory factory) {
        this.factory = factory;
        defaultContext = new SimpleScriptContext();
        // set special values
        put(LANGUAGE_VERSION, "1.0");
        put(LANGUAGE, STR_THISLANGUAGE);
        put(ENGINE, MY_NAME);
        put(ENGINE_VERSION, __ENGINE_VERSION__);
        put(ARGV, ""); // TO DO: set correct value
        put(FILENAME, ""); // TO DO: set correct value
        put(NAME, MY_SHORT_NAME);
        /*
         * I am not sure if this is correct; we need to check if
         * the name really is THREADING. I have no idea why there is
         * no constant as for the other keys
         */
        put("THREADING", null);
        this.interpreter = new Jasic();
    }

    /** */
    private Jasic interpreter;

    @Override
    public Object eval(String script) throws ScriptException {
        return eval(script, getContext());
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        try {
            interpreter.interpret(script);
            return null; // TODO
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(String script, Bindings bindings) throws ScriptException {
        Bindings current = getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        getContext().setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        Object result = eval(script);
        getContext().setBindings(current, ScriptContext.ENGINE_SCOPE);
        return result;
    }

    @Override
    public Object eval(Reader reader) throws ScriptException {
        return eval(getScriptFromReader(reader));
    }

    @Override
    public Object eval(Reader reader, ScriptContext scriptContext) throws ScriptException {
        return eval(getScriptFromReader(reader), scriptContext);
    }

    @Override
    public Object eval(Reader reader, Bindings bindings) throws ScriptException {
        return eval(getScriptFromReader(reader), bindings);
    }

    @Override
    public void put(String key, Object value) {
        getBindings(ScriptContext.ENGINE_SCOPE).put(key, value);
    }

    @Override
    public Object get(String key) {
        return getBindings(ScriptContext.ENGINE_SCOPE).get(key);
    }

    @Override
    public Bindings getBindings(int scope) {
        return getContext().getBindings(scope);
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {
        getContext().setBindings(bindings, scope);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptContext getContext() {
        return defaultContext;
    }

    @Override
    public void setContext(ScriptContext context) {
        defaultContext = context;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

    /** */
    private static String getScriptFromReader(Reader reader) {
        try {
            StringWriter script = new StringWriter();
            int data;
            while ((data = reader.read()) != -1) {
                script.write(data);
            }
            script.flush();
            return script.toString();
        } catch (IOException e) {
e.printStackTrace(System.err);
            return null;
        }
    }
}

/* */
