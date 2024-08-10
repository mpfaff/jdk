/*
 * Copyright (c) 2002, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.internal.access;

import jdk.internal.vm.annotation.Stable;

import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import java.io.ObjectInputFilter;
import java.lang.invoke.MethodHandles;
import java.lang.module.ModuleDescriptor;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;
import java.util.jar.JarFile;
import java.io.Console;
import java.io.FileDescriptor;
import java.io.FilePermission;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.ProtectionDomain;
import java.security.Signature;
import javax.security.auth.x500.X500Principal;

/** A repository of "shared secrets", which are a mechanism for
    calling implementation-private methods in another package without
    using reflection. A package-private class implements a public
    interface and provides the ability to call package-private methods
    within that package; the object implementing that interface is
    provided through a third package to which access is restricted.
    This framework avoids the primary disadvantage of using reflection
    for this purpose, namely the loss of compile-time checking. */

public class SharedSecrets {
    @Stable
    private static JavaAWTAccess javaAWTAccess;
    @Stable
    private static JavaAWTFontAccess javaAWTFontAccess;
    @Stable
    private static JavaBeansAccess javaBeansAccess;
    @Stable
    private static JavaLangAccess javaLangAccess;
    @Stable
    private static JavaLangInvokeAccess javaLangInvokeAccess;
    @Stable
    private static JavaLangModuleAccess javaLangModuleAccess;
    @Stable
    private static JavaLangRefAccess javaLangRefAccess;
    @Stable
    private static JavaLangReflectAccess javaLangReflectAccess;
    @Stable
    private static JavaIOAccess javaIOAccess;
    @Stable
    private static JavaIOPrintStreamAccess javaIOPrintStreamAccess;
    @Stable
    private static JavaIOPrintWriterAccess javaIOPrintWriterAccess;
    @Stable
    private static JavaIOFileDescriptorAccess javaIOFileDescriptorAccess;
    @Stable
    private static JavaIOFilePermissionAccess javaIOFilePermissionAccess;
    @Stable
    private static JavaIORandomAccessFileAccess javaIORandomAccessFileAccess;
    @Stable
    private static JavaObjectInputStreamReadString javaObjectInputStreamReadString;
    @Stable
    private static JavaObjectInputStreamAccess javaObjectInputStreamAccess;
    @Stable
    private static JavaObjectInputFilterAccess javaObjectInputFilterAccess;
    @Stable
    private static JavaNetInetAddressAccess javaNetInetAddressAccess;
    @Stable
    private static JavaNetHttpCookieAccess javaNetHttpCookieAccess;
    @Stable
    private static JavaNetUriAccess javaNetUriAccess;
    @Stable
    private static JavaNetURLAccess javaNetURLAccess;
    @Stable
    private static JavaNioAccess javaNioAccess;
    @Stable
    private static JavaUtilCollectionAccess javaUtilCollectionAccess;
    @Stable
    private static JavaUtilConcurrentTLRAccess javaUtilConcurrentTLRAccess;
    @Stable
    private static JavaUtilConcurrentFJPAccess javaUtilConcurrentFJPAccess;
    @Stable
    private static JavaUtilJarAccess javaUtilJarAccess;
    @Stable
    private static JavaUtilZipFileAccess javaUtilZipFileAccess;
    @Stable
    private static JavaUtilResourceBundleAccess javaUtilResourceBundleAccess;
    @Stable
    private static JavaSecurityAccess javaSecurityAccess;
    @Stable
    private static JavaSecurityPropertiesAccess javaSecurityPropertiesAccess;
    @Stable
    private static JavaSecuritySignatureAccess javaSecuritySignatureAccess;
    @Stable
    private static JavaSecuritySpecAccess javaSecuritySpecAccess;
    @Stable
    private static JavaxCryptoSealedObjectAccess javaxCryptoSealedObjectAccess;
    @Stable
    private static JavaxCryptoSpecAccess javaxCryptoSpecAccess;
    @Stable
    private static JavaxSecurityAccess javaxSecurityAccess;
    @Stable
    private static JavaTemplateAccess javaTemplateAccess;

    private static void checkNotAssigned(Object currentValue) {
        if (currentValue != null) {
            throw new IllegalStateException("Already assigned");
        }
    }

    public static void setJavaUtilCollectionAccess(JavaUtilCollectionAccess juca) {
        checkNotAssigned(javaUtilCollectionAccess);
        javaUtilCollectionAccess = juca;
    }

    public static JavaUtilCollectionAccess getJavaUtilCollectionAccess() {
        var access = javaUtilCollectionAccess;
        if (access == null) {
            try {
                Class.forName("java.util.ImmutableCollections$Access", true, null);
                access = javaUtilCollectionAccess;
            } catch (ClassNotFoundException e) {}
        }
        return access;
    }

    public static void setJavaUtilConcurrentTLRAccess(JavaUtilConcurrentTLRAccess access) {
        checkNotAssigned(javaUtilConcurrentTLRAccess);
        javaUtilConcurrentTLRAccess = access;
    }

    public static JavaUtilConcurrentTLRAccess getJavaUtilConcurrentTLRAccess() {
        var access = javaUtilConcurrentTLRAccess;
        if (access == null) {
            try {
                Class.forName("java.util.concurrent.ThreadLocalRandom$Access", true, null);
                access = javaUtilConcurrentTLRAccess;
            } catch (ClassNotFoundException e) {}
        }
        return access;
    }

    public static void setJavaUtilConcurrentFJPAccess(JavaUtilConcurrentFJPAccess access) {
        checkNotAssigned(javaUtilConcurrentFJPAccess);
        javaUtilConcurrentFJPAccess = access;
    }

    public static JavaUtilConcurrentFJPAccess getJavaUtilConcurrentFJPAccess() {
        var access = javaUtilConcurrentFJPAccess;
        if (access == null) {
            ensureClassInitialized(ForkJoinPool.class);
            access = javaUtilConcurrentFJPAccess;
        }
        return access;
    }

    public static JavaUtilJarAccess javaUtilJarAccess() {
        var access = javaUtilJarAccess;
        if (access == null) {
            // Ensure JarFile is initialized; we know that this class
            // provides the shared secret
            ensureClassInitialized(JarFile.class);
            access = javaUtilJarAccess;
        }
        return access;
    }

    public static void setJavaUtilJarAccess(JavaUtilJarAccess access) {
        checkNotAssigned(javaUtilJarAccess);
        javaUtilJarAccess = access;
    }

    public static void setJavaLangAccess(JavaLangAccess jla) {
        checkNotAssigned(javaLangAccess);
        javaLangAccess = jla;
    }

    public static JavaLangAccess getJavaLangAccess() {
        return javaLangAccess;
    }

    public static void setJavaLangInvokeAccess(JavaLangInvokeAccess jlia) {
        checkNotAssigned(javaLangInvokeAccess);
        javaLangInvokeAccess = jlia;
    }

    public static JavaLangInvokeAccess getJavaLangInvokeAccess() {
        var access = javaLangInvokeAccess;
        if (access == null) {
            try {
                Class.forName("java.lang.invoke.MethodHandleImpl", true, null);
                access = javaLangInvokeAccess;
            } catch (ClassNotFoundException e) {}
        }
        return access;
    }

    public static void setJavaLangModuleAccess(JavaLangModuleAccess jlrma) {
        checkNotAssigned(javaLangModuleAccess);
        javaLangModuleAccess = jlrma;
    }

    public static JavaLangModuleAccess getJavaLangModuleAccess() {
        var access = javaLangModuleAccess;
        if (access == null) {
            ensureClassInitialized(ModuleDescriptor.class);
            access = javaLangModuleAccess;
        }
        return access;
    }

    public static void setJavaLangRefAccess(JavaLangRefAccess jlra) {
        checkNotAssigned(javaLangRefAccess);
        javaLangRefAccess = jlra;
    }

    public static JavaLangRefAccess getJavaLangRefAccess() {
        return javaLangRefAccess;
    }

    public static void setJavaLangReflectAccess(JavaLangReflectAccess jlra) {
        checkNotAssigned(javaLangReflectAccess);
        javaLangReflectAccess = jlra;
    }

    public static JavaLangReflectAccess getJavaLangReflectAccess() {
        return javaLangReflectAccess;
    }

    public static void setJavaNetUriAccess(JavaNetUriAccess jnua) {
        checkNotAssigned(javaNetUriAccess);
        javaNetUriAccess = jnua;
    }

    public static JavaNetUriAccess getJavaNetUriAccess() {
        var access = javaNetUriAccess;
        if (access == null) {
            ensureClassInitialized(java.net.URI.class);
            access = javaNetUriAccess;
        }
        return access;
    }

    public static void setJavaNetURLAccess(JavaNetURLAccess jnua) {
        checkNotAssigned(javaNetURLAccess);
        javaNetURLAccess = jnua;
    }

    public static JavaNetURLAccess getJavaNetURLAccess() {
        var access = javaNetURLAccess;
        if (access == null) {
            ensureClassInitialized(java.net.URL.class);
            access = javaNetURLAccess;
        }
        return access;
    }

    public static void setJavaNetInetAddressAccess(JavaNetInetAddressAccess jna) {
        checkNotAssigned(javaNetInetAddressAccess);
        javaNetInetAddressAccess = jna;
    }

    public static JavaNetInetAddressAccess getJavaNetInetAddressAccess() {
        var access = javaNetInetAddressAccess;
        if (access == null) {
            ensureClassInitialized(java.net.InetAddress.class);
            access = javaNetInetAddressAccess;
        }
        return access;
    }

    public static void setJavaNetHttpCookieAccess(JavaNetHttpCookieAccess a) {
        checkNotAssigned(javaNetHttpCookieAccess);
        javaNetHttpCookieAccess = a;
    }

    public static JavaNetHttpCookieAccess getJavaNetHttpCookieAccess() {
        var access = javaNetHttpCookieAccess;
        if (access == null) {
            ensureClassInitialized(java.net.HttpCookie.class);
            access = javaNetHttpCookieAccess;
        }
        return access;
    }

    public static void setJavaNioAccess(JavaNioAccess jna) {
        checkNotAssigned(javaNioAccess);
        javaNioAccess = jna;
    }

    public static JavaNioAccess getJavaNioAccess() {
        var access = javaNioAccess;
        if (access == null) {
            // Ensure java.nio.Buffer is initialized, which provides the
            // shared secret.
            ensureClassInitialized(java.nio.Buffer.class);
            access = javaNioAccess;
        }
        return access;
    }

    public static void setJavaIOAccess(JavaIOAccess jia) {
        checkNotAssigned(javaIOAccess);
        javaIOAccess = jia;
    }

    public static JavaIOAccess getJavaIOAccess() {
        var access = javaIOAccess;
        if (access == null) {
            ensureClassInitialized(Console.class);
            access = javaIOAccess;
        }
        return access;
    }

    public static void setJavaIOCPrintWriterAccess(JavaIOPrintWriterAccess a) {
        checkNotAssigned(javaIOPrintWriterAccess);
        javaIOPrintWriterAccess = a;
    }

    public static JavaIOPrintWriterAccess getJavaIOPrintWriterAccess() {
        var access = javaIOPrintWriterAccess;
        if (access == null) {
            ensureClassInitialized(PrintWriter.class);
            access = javaIOPrintWriterAccess;
        }
        return access;
    }

    public static void setJavaIOCPrintStreamAccess(JavaIOPrintStreamAccess a) {
        checkNotAssigned(javaIOPrintStreamAccess);
        javaIOPrintStreamAccess = a;
    }

    public static JavaIOPrintStreamAccess getJavaIOPrintStreamAccess() {
        var access = javaIOPrintStreamAccess;
        if (access == null) {
            ensureClassInitialized(PrintStream.class);
            access = javaIOPrintStreamAccess;
        }
        return access;
    }

    public static void setJavaIOFileDescriptorAccess(JavaIOFileDescriptorAccess jiofda) {
        checkNotAssigned(javaIOFileDescriptorAccess);
        javaIOFileDescriptorAccess = jiofda;
    }

    public static JavaIOFilePermissionAccess getJavaIOFilePermissionAccess() {
        var access = javaIOFilePermissionAccess;
        if (access == null) {
            ensureClassInitialized(FilePermission.class);
            access = javaIOFilePermissionAccess;
        }
        return access;
    }

    public static void setJavaIOFilePermissionAccess(JavaIOFilePermissionAccess jiofpa) {
        checkNotAssigned(javaIOFilePermissionAccess);
        javaIOFilePermissionAccess = jiofpa;
    }

    public static JavaIOFileDescriptorAccess getJavaIOFileDescriptorAccess() {
        var access = javaIOFileDescriptorAccess;
        if (access == null) {
            ensureClassInitialized(FileDescriptor.class);
            access = javaIOFileDescriptorAccess;
        }
        return access;
    }

    public static void setJavaSecurityAccess(JavaSecurityAccess jsa) {
        checkNotAssigned(javaSecurityAccess);
        javaSecurityAccess = jsa;
    }

    public static JavaSecurityAccess getJavaSecurityAccess() {
        var access = javaSecurityAccess;
        if (access == null) {
            ensureClassInitialized(ProtectionDomain.class);
            access = javaSecurityAccess;
        }
        return access;
    }

    public static void setJavaSecurityPropertiesAccess(JavaSecurityPropertiesAccess jspa) {
        checkNotAssigned(javaSecurityPropertiesAccess);
        javaSecurityPropertiesAccess = jspa;
    }

    public static JavaSecurityPropertiesAccess getJavaSecurityPropertiesAccess() {
        var access = javaSecurityPropertiesAccess;
        if (access == null) {
            ensureClassInitialized(Security.class);
            access = javaSecurityPropertiesAccess;
        }
        return access;
    }

    public static JavaUtilZipFileAccess getJavaUtilZipFileAccess() {
        var access = javaUtilZipFileAccess;
        if (access == null) {
            ensureClassInitialized(java.util.zip.ZipFile.class);
            access = javaUtilZipFileAccess;
        }
        return access;
    }

    public static void setJavaUtilZipFileAccess(JavaUtilZipFileAccess access) {
        checkNotAssigned(javaUtilZipFileAccess);
        javaUtilZipFileAccess = access;
    }

    public static void setJavaAWTAccess(JavaAWTAccess jaa) {
        checkNotAssigned(javaAWTAccess);
        javaAWTAccess = jaa;
    }

    public static JavaAWTAccess getJavaAWTAccess() {
        // this may return null in which case calling code needs to
        // provision for.
        return javaAWTAccess;
    }

    public static void setJavaAWTFontAccess(JavaAWTFontAccess jafa) {
        checkNotAssigned(javaAWTFontAccess);
        javaAWTFontAccess = jafa;
    }

    public static JavaAWTFontAccess getJavaAWTFontAccess() {
        // this may return null in which case calling code needs to
        // provision for.
        return javaAWTFontAccess;
    }

    public static JavaBeansAccess getJavaBeansAccess() {
        return javaBeansAccess;
    }

    public static void setJavaBeansAccess(JavaBeansAccess access) {
        checkNotAssigned(javaBeansAccess);
        javaBeansAccess = access;
    }

    public static JavaUtilResourceBundleAccess getJavaUtilResourceBundleAccess() {
        var access = javaUtilResourceBundleAccess;
        if (access == null) {
            ensureClassInitialized(ResourceBundle.class);
            access = javaUtilResourceBundleAccess;
        }
        return access;
    }

    public static void setJavaUtilResourceBundleAccess(JavaUtilResourceBundleAccess access) {
        checkNotAssigned(javaUtilResourceBundleAccess);
        javaUtilResourceBundleAccess = access;
    }

    public static JavaObjectInputStreamReadString getJavaObjectInputStreamReadString() {
        var access = javaObjectInputStreamReadString;
        if (access == null) {
            ensureClassInitialized(ObjectInputStream.class);
            access = javaObjectInputStreamReadString;
        }
        return access;
    }

    public static void setJavaObjectInputStreamReadString(JavaObjectInputStreamReadString access) {
        checkNotAssigned(javaObjectInputStreamReadString);
        javaObjectInputStreamReadString = access;
    }

    public static JavaObjectInputStreamAccess getJavaObjectInputStreamAccess() {
        var access = javaObjectInputStreamAccess;
        if (access == null) {
            ensureClassInitialized(ObjectInputStream.class);
            access = javaObjectInputStreamAccess;
        }
        return access;
    }

    public static void setJavaObjectInputStreamAccess(JavaObjectInputStreamAccess access) {
        checkNotAssigned(javaObjectInputStreamAccess);
        javaObjectInputStreamAccess = access;
    }

    public static JavaObjectInputFilterAccess getJavaObjectInputFilterAccess() {
        var access = javaObjectInputFilterAccess;
        if (access == null) {
            ensureClassInitialized(ObjectInputFilter.Config.class);
            access = javaObjectInputFilterAccess;
        }
        return access;
    }

    public static void setJavaObjectInputFilterAccess(JavaObjectInputFilterAccess access) {
        checkNotAssigned(javaObjectInputFilterAccess);
        javaObjectInputFilterAccess = access;
    }

    public static void setJavaIORandomAccessFileAccess(JavaIORandomAccessFileAccess jirafa) {
        checkNotAssigned(javaIORandomAccessFileAccess);
        javaIORandomAccessFileAccess = jirafa;
    }

    public static JavaIORandomAccessFileAccess getJavaIORandomAccessFileAccess() {
        var access = javaIORandomAccessFileAccess;
        if (access == null) {
            ensureClassInitialized(RandomAccessFile.class);
            access = javaIORandomAccessFileAccess;
        }
        return access;
    }

    public static void setJavaSecuritySignatureAccess(JavaSecuritySignatureAccess jssa) {
        checkNotAssigned(javaSecuritySignatureAccess);
        javaSecuritySignatureAccess = jssa;
    }

    public static JavaSecuritySignatureAccess getJavaSecuritySignatureAccess() {
        var access = javaSecuritySignatureAccess;
        if (access == null) {
            ensureClassInitialized(Signature.class);
            access = javaSecuritySignatureAccess;
        }
        return access;
    }

    public static void setJavaSecuritySpecAccess(JavaSecuritySpecAccess jssa) {
        checkNotAssigned(javaSecuritySpecAccess);
        javaSecuritySpecAccess = jssa;
    }

    public static JavaSecuritySpecAccess getJavaSecuritySpecAccess() {
        var access = javaSecuritySpecAccess;
        if (access == null) {
            ensureClassInitialized(EncodedKeySpec.class);
            access = javaSecuritySpecAccess;
        }
        return access;
    }

    public static void setJavaxCryptoSpecAccess(JavaxCryptoSpecAccess jcsa) {
        checkNotAssigned(javaxCryptoSpecAccess);
        javaxCryptoSpecAccess = jcsa;
    }

    public static JavaxCryptoSpecAccess getJavaxCryptoSpecAccess() {
        var access = javaxCryptoSpecAccess;
        if (access == null) {
            ensureClassInitialized(SecretKeySpec.class);
            access = javaxCryptoSpecAccess;
        }
        return access;
    }

    public static void setJavaxCryptoSealedObjectAccess(JavaxCryptoSealedObjectAccess jcsoa) {
        checkNotAssigned(javaxCryptoSealedObjectAccess);
        javaxCryptoSealedObjectAccess = jcsoa;
    }

    public static JavaxCryptoSealedObjectAccess getJavaxCryptoSealedObjectAccess() {
        var access = javaxCryptoSealedObjectAccess;
        if (access == null) {
            ensureClassInitialized(SealedObject.class);
            access = javaxCryptoSealedObjectAccess;
        }
        return access;
    }

    public static void setJavaxSecurityAccess(JavaxSecurityAccess jsa) {
        checkNotAssigned(javaxSecurityAccess);
        javaxSecurityAccess = jsa;
    }

    public static JavaxSecurityAccess getJavaxSecurityAccess() {
        var access = javaxSecurityAccess;
        if (access == null) {
            ensureClassInitialized(X500Principal.class);
            access = javaxSecurityAccess;
        }
        return access;
    }

    public static void setJavaTemplateAccess(JavaTemplateAccess jta) {
        checkNotAssigned(javaTemplateAccess);
        javaTemplateAccess = jta;
    }

    public static JavaTemplateAccess getJavaTemplateAccess() {
        var access = javaTemplateAccess;
        if (access == null) {
            try {
                Class.forName("java.lang.runtime.TemplateSupport", true, null);
                access = javaTemplateAccess;
            } catch (ClassNotFoundException e) {}
        }
        return access;
    }

    private static void ensureClassInitialized(Class<?> c) {
        try {
            MethodHandles.lookup().ensureInitialized(c);
        } catch (IllegalAccessException e) {}
    }
}
