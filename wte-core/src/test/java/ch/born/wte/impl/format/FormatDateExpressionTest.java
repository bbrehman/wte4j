package ch.born.wte.impl.format;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.born.wte.FormatterFactory;
import ch.born.wte.impl.expression.ResolverFactoryImpl;
import ch.born.wte.impl.expression.ValueResolver;
import ch.born.wte.impl.expression.ValueResolverFactory;

import static org.junit.Assert.assertEquals;

/**
 * Ueberprueft, dass die Formatierungen von Datum und Zeit via
 * Formatierungsanweisung funktioniert.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { FormatDateExpressionTest.Config.class })
public class FormatDateExpressionTest {

	private static final Locale LOCALE = Locale.GERMAN;

	private static final Map<String, Class<?>> ELEMENTS = Collections
			.<String, Class<?>> singletonMap("value", Date.class);

	@Autowired
	private FormatterFactory formatterRegistry;

	public ValueResolverFactory templateContext() {
		return new ResolverFactoryImpl(LOCALE, formatterRegistry, ELEMENTS);
	}

	@Test()
	public void formateDateShortTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:date(short) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("15.02.1977", formated);
	}

	@Test()
	public void formateDateMediumTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:date(medium) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("15. Feb. 1977", formated);
	}

	@Test()
	public void formateDateLongTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:date(long) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("15. Februar 1977", formated);
	}

	@Test()
	public void formateTimeShortTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:time(short) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.clear();
		calendar.set(1977, 01, 15, 22, 33, 44);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("22:33", formated);
	}

	@Test()
	public void formateTimeMediumTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:time(medium) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15, 22, 33, 44);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("22:33:44", formated);
	}

	@Test()
	public void formateTimeLongTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:time(long) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15, 22, 33, 44);
		calendar.set(Calendar.MILLISECOND, 123);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("22:33:44.123 MEZ", formated);
	}

	@Test()
	public void formateDateTimeShortTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:dateTime(short, short) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.clear();
		calendar.set(1977, 01, 15, 22, 33, 44);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("15.02.1977 22:33", formated);
	}

	@Test()
	public void formateDateTimeMediumTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:dateTime(medium, medium) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15, 22, 33, 44);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("15. Feb. 1977 22:33:44", formated);
	}

	@Test()
	public void formateDateTimeLongTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:dateTime(long, long) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15, 22, 33, 44);
		calendar.set(Calendar.MILLISECOND, 123);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("15. Februar 1977 22:33:44.123 MEZ", formated);
	}

	@Test()
	public void formateDateTimeDifferentFormatTest() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:dateTime(short, medium) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15, 22, 33, 44);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("15.02.1977 22:33:44", formated);
	}

	@Test()
	public void formateDateTimeWithPattern() {
		ValueResolver<String> expression = templateContext()
				.createStringResolver("format:customDateTime(yyyy) value");
		Calendar calendar = Calendar.getInstance(LOCALE);
		calendar.set(1977, 01, 15, 22, 33, 44);
		String formated = expression.resolve(new SingleValueDataModel(calendar
				.getTime()));
		assertEquals("1977", formated);
	}

	@Configuration
	public static class Config {
		@Bean
		public FormatterFactory formatterRegistry() {
			return new FormatterRegistry();
		}
	}

}