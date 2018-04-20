/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.regex;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.InteropException;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

/**
 * {@link ForeignRegexCompiler} wraps a {@link TruffleObject} that is compatible with
 * {@link RegexCompiler} and lets us use it as if it were an actual {@link RegexCompiler}.
 * 
 * @author Jirka Marsik <jiri.marsik@oracle.com>
 */
public class ForeignRegexCompiler extends RegexCompiler {

    private final TruffleObject foreignCompiler;

    private final Node executeNode = Message.createExecute(2).createNode();

    public ForeignRegexCompiler(TruffleObject foreignCompiler) {
        this.foreignCompiler = foreignCompiler;
    }

    /**
     * Wraps the supplied {@link TruffleObject} in a {@link ForeignRegexCompiler}, unless it already
     * is a {@link RegexCompiler}. Use this when accepting {@link RegexCompiler}s over Truffle
     * interop.
     */
    public static RegexCompiler importRegexCompiler(TruffleObject regexCompiler) {
        return regexCompiler instanceof RegexCompiler ? (RegexCompiler) regexCompiler : new ForeignRegexCompiler(regexCompiler);
    }

    @Override
    public TruffleObject compile(RegexSource source) throws RegexSyntaxException {
        try {
            return (TruffleObject) ForeignAccess.sendExecute(executeNode, foreignCompiler, source.getPattern(), source.getFlags().toString());
        } catch (InteropException ex) {
            throw ex.raise();
        }
    }
}