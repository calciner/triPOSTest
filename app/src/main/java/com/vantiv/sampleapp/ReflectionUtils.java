package com.vantiv.sampleapp;

import android.util.Log;

import com.vantiv.triposmobilesdk.EmvTag;
import com.vantiv.triposmobilesdk.TlvCollection;
 import com.vantiv.triposmobilesdk.utilities.BigDecimalUtility;
import com.vantiv.triposmobilesdk.utilities.ByteArrayUtility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ReflectionUtils
{

    public static Field[] getDeclaredFieldsForObject(Object obj)
    {
        ArrayList<Field> filteredFields = new ArrayList<>();

        for (Class theClass = obj.getClass(); theClass != null && theClass.getPackage().getName().startsWith("com.vantiv.triposmobilesdk"); theClass = theClass.getSuperclass())
        {
            Field[] fields = theClass.getDeclaredFields();

            for (Field field : fields)
            {
                if (!field.getName().equalsIgnoreCase("$change") && !field.getName().equalsIgnoreCase("serialversionuid"))
                {
                    filteredFields.add(field);
                }
            }
        }

        return filteredFields.toArray(new Field[filteredFields.size()]);
    }

    public static String recursiveToString(Object obj) throws IllegalAccessException
    {
        final String newLine = "\n\n";
        final String tab = "\t\t\t\t";

        if (obj == null)
        {
            return "null" + newLine;
        }

        // Base case
        if (obj.getClass().isPrimitive()
                || obj.getClass().isAssignableFrom(String.class)
                || obj.getClass().isEnum())
        {
            return obj.toString() + newLine;
        }

        if (obj.getClass() == BigDecimal.class)
        {
            // currently, the only place we use BigDecimal's are for amounts
            return BigDecimalUtility.formatAsCurrency((BigDecimal) obj) + newLine;
        }

        if (obj.getClass() == TlvCollection.class)
        {
            TlvCollection tlvCollection = (TlvCollection)obj;

            StringBuilder tagResult = new StringBuilder(obj.getClass().getSimpleName() + ": ..." + newLine);

            for (Map.Entry<EmvTag, byte[]> tag:
                    tlvCollection.entrySet())
            {
                tagResult.append(String.format("%02X: %s", tag.getKey().getTagValue(), ByteArrayUtility.byteArrayToHexString(tag.getValue()))).append(newLine);
            }

            return tagResult.toString();
        }

        if (obj.getClass() == ArrayList.class)
        {
            ArrayList arrayList = (ArrayList)obj;

            StringBuilder stringBuilder = new StringBuilder(obj.getClass().getSimpleName() + ": ..." + newLine);

            for (Object item: arrayList)
            {
                stringBuilder.append(recursiveToString(item));
            }

            return stringBuilder.toString();
        }

        if (isAndroidType(obj.getClass()) || isWrapperType(obj.getClass()))
        {
            return obj.toString() + newLine;
        }

        StringBuilder result = new StringBuilder(obj.getClass().getSimpleName() + ": ..." + newLine);

        // Recursive step
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods)
        {
            if ((method.getName().startsWith("get")
                    || method.getName().startsWith("is")
                    || method.getName().startsWith("was"))
                    && !method.getName().startsWith("getType")
                    && method.getDeclaringClass() != Object.class)
            {
                try
                {
                    Object resultObj = method.invoke(obj);

                    if (resultObj instanceof String)
                    {
                        result.append(tab).append(method.getName()).append(": ").append(resultObj).append(newLine);//.append(": ").append(recursiveToString(field.get(obj)));
                    }
                    else
                    {
                        result.append(tab).append(method.getName()).append(": ").append(recursiveToString(resultObj));
                    }
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    Log.e(ReflectionUtils.class.getSimpleName(), String.format("recursiveToString : Wrong number of arguments was passed to method: %s", method.getName()));
                }
            }
        }

        return result.toString();
    }

    public static Object recursiveToDict(Object obj) throws IllegalAccessException
    {
        if (obj == null
                || isAndroidType(obj.getClass())
                || isWrapperType(obj.getClass()))
        {
            return null;
        }

        // Base case
        if (obj.getClass().isPrimitive()
                || obj.getClass().isAssignableFrom(String.class)
                || obj.getClass().isEnum()
                || obj.getClass() == BigDecimal.class)
        {
            return obj;
        }

        LinkedHashMap<String, Object> dictionary = new LinkedHashMap<>();

        StringBuilder className = new StringBuilder(obj.getClass().getSimpleName());

        // Recursive step
        Field[] fields = getDeclaredFieldsForObject(obj);
        for (Field field : fields)
        {
            field.setAccessible(true);

            dictionary.put(field.getName(), recursiveToDict(field.get(obj)));
        }

        return dictionary;
    }

    public static String getPackageName(String typeName)
    {
        String childPackageName = "";

        // Get the childField's parent package name
        // Used in order to check for enum types
        String[] childTypeNameSplit = typeName.split("\\.", -1); // Split the package name by periods

        for (int i = 0; i < childTypeNameSplit.length - 1; i++)
        {
            childPackageName += childTypeNameSplit[i];

            if (i < childTypeNameSplit.length - 2)
            {
                childPackageName += ".";
            }
        }

        return childPackageName;
    }

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    private static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(Date.class);
        return ret;
    }

    private static boolean isAndroidType(Object obj)
    {
        String classToString = obj.toString();
        return classToString.contains("com.android");
    }
}
