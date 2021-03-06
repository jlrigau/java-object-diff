/*
 * Copyright 2012 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import org.testng.annotations.*;

import java.lang.reflect.*;
import java.util.*;

import static de.danielbechler.diff.extension.AssertionsExtensions.*;
import static de.danielbechler.diff.extension.MockitoExtensions.*;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

/** @author Daniel Bechler */
public class InstancesTest
{
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetTypeThrowsExceptionForIncompatibleTypes() throws Exception
	{
		new Instances(RootAccessor.getInstance(), "foo", 1, null).getType();
	}

	@Test
	public void testGetTypeReturnsCollectionTypeForDifferentCollectionImplementations() throws Exception
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), new ArrayList<Object>(), new LinkedHashSet(), null);
		final Class<?> type = instances.getType();
		assertThat(type == Collection.class);
	}

	@Test
	public void testGetTypeReturnsMapTypeForDifferentMapImplementations() throws Exception
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), new HashMap<Object, Object>(), new TreeMap<Object, Object>(), null);
		final Class<?> type = instances.getType();
		assertThat(type == Map.class);
	}

	@Test
	public void testGetTypeReturnsTypeOfTypeAwareAccessor() throws Exception
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(Long.class);
		final Instances instances = new Instances(typeAwareAccessor, 0, 0, 0);
		assertThat(instances.getType()).is(ofType(Long.class));
	}

	private static <T> TypeAwareAccessor mockTypeAwareAccessorOfType(final Class<T> clazz)
	{
		final TypeAwareAccessor typeAwareAccessor = mock(TypeAwareAccessor.class);
		when(typeAwareAccessor.getType()).then(returnClass(clazz));
		return typeAwareAccessor;
	}

	@Test
	public void testAreNull_returns_true_when_base_and_working_are_null()
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), null, null, null);
		assertThat(instances.areNull()).isTrue();
	}

	@Test
	public void testAreNull_returns_false_when_base_is_not_null()
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), null, "", null);
		assertThat(instances.areNull()).isFalse();
	}

	@Test
	public void testAreNull_returns_false_when_working_is_not_null()
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), "", null, null);
		assertThat(instances.areNull()).isFalse();
	}

	@Test
	public void testIsPrimitiveTypeReturnsTrueForPrimitiveType()
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(long.class);
		assertThat(new Instances(typeAwareAccessor, 1L, 2L, 0L).isPrimitiveType()).isTrue();
	}

	@Test
	public void testIsPrimitiveTypeReturnsFalseForPrimitiveWrapperType()
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(Long.class);
		assertThat(new Instances(typeAwareAccessor, 1L, 2L, 0L).isPrimitiveType()).isFalse();
	}

	@Test
	public void testGetFreshReturnsZeroForPrimitiveNumericTypeIfUndefined() throws Exception
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(long.class);
		assertThat(new Instances(typeAwareAccessor, 0, 0, null).getFresh()).isEqualTo(0);
	}

	@Test
	public void testGetFreshReturnsDefinedDefaultValueForPrimitiveNumericType() throws Exception
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(long.class);
		assertThat(new Instances(typeAwareAccessor, 0, 0, 1337).getFresh()).isEqualTo(1337);
	}

	@Test
	public void testGetFreshReturnsZeroForPrimitiveBooleanTypeIfUndefined() throws Exception
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(boolean.class);
		assertThat(new Instances(typeAwareAccessor, true, true, null).getFresh()).isEqualTo(false);
	}

	@Test
	public void testGetFreshReturnsNullForDefaultLessPrimitiveWrapperType() throws Exception
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(Long.class);
		assertThat(new Instances(typeAwareAccessor, 0L, 0L, null).getFresh()).isNull();
	}

	@Test
	public void testGetFreshReturnsDefinedDefaultValueForPrimitiveBooleanType() throws Exception
	{
		final TypeAwareAccessor typeAwareAccessor = mockTypeAwareAccessorOfType(boolean.class);
		assertThat(new Instances(typeAwareAccessor, true, true, true).getFresh()).isEqualTo(true);
	}

	@Test
	public void testIsPrimitiveTypeReturnsPrimitiveClassForPrimitiveType() throws Exception
	{
		final Method readMethod = getClass().getDeclaredMethod("getTestValue");
		final PropertyAccessor accessor = new PropertyAccessor("testValue", readMethod, null);
		final Instances instances = new Instances(accessor, 1L, 2L, 0L);
		assertThat(instances.getType() == long.class).isTrue();
	}

	@Test
	public void testIsPrimitiveTypeReturnsFalseForComplexType()
	{
		assertThat(new Instances(RootAccessor.getInstance(), "1", "2", null).isPrimitiveType()).isFalse();
	}

	@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
	public long getTestValue()
	{
		return 0L;
	}
}
