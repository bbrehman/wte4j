package ch.born.wte.impl;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ch.born.wte.FormatterFactory;
import ch.born.wte.Template;
import ch.born.wte.TemplateBuildException;
import ch.born.wte.TemplateBuilder;
import ch.born.wte.User;
import ch.born.wte.WteException;
import ch.born.wte.WteModelService;
import ch.born.wte.impl.word.Docx4JWordTemplate;
import ch.born.wte.impl.word.PlainTextContent;

public class WordTemplateBuilder<E> implements ch.born.wte.TemplateBuilder<E> {

	private WteModelService modelService;
	private FormatterFactory formatterFactory;
	private Class<E> inputType;
	private User user;
	private String documentName;
	private String language;
	private Map<String, String> modelProperties = new HashMap<String, String>();

	/**
	 * @param formatterFactory
	 * @param service
	 * @param inputType
	 * @throws TemplateBuildException
	 *             thrown if any of the parameters formatterFactory, service or
	 *             inputType are missing
	 */
	public WordTemplateBuilder(FormatterFactory formatterFactory,
			WteModelService service, Class<E> inputType)
			throws TemplateBuildException {
		if (formatterFactory == null || service == null || inputType == null) {
			throw new TemplateBuildException(
					"formatterFactory, service and inputType must not be null");
		}
		this.modelService = service;
		this.formatterFactory = formatterFactory;
		this.inputType = inputType;
		this.user = new User("", "");
		this.documentName = "new_template";
		this.language = Locale.getDefault().getLanguage();
	}

	@Override
	public TemplateBuilder<E> setProperties(Map<String, String> modelProperties) {
		this.modelProperties = modelProperties;
		return this;
	}

	@Override
	public TemplateBuilder<E> setDocumentName(String documentName) {
		this.documentName = documentName;
		return this;
	}

	@Override
	public TemplateBuilder<E> setAuthor(User user) {
		this.user = user;
		return this;
	}

	@Override
	public TemplateBuilder<E> setLanguage(String language) {
		this.language = language;
		return this;
	}

	/**
	 * @throws TemplateBuildException
	 *             thrown if a required property is missing or if any of the
	 *             parameters documentName, language or author's userId are
	 *             missing
	 */
	public Template<E> build() throws TemplateBuildException {
		validateBeforeBuild();
		PersistentTemplate template = new PersistentTemplate();
		template.setDocumentName(documentName);
		template.setLanguage(language);
		template.setInputType(inputType);
		template.setProperties(modelProperties);
		template.setEditor(user);
		template.setContent(createBasicTemplate());
		Date date = new Date();
		template.setEditedAt(date);
		template.setCreatedAt(date);

		return new WordTemplate<E>(template, modelService, formatterFactory);
	}

	byte[] createBasicTemplate() {
		try {
			Docx4JWordTemplate basicTemplate = new Docx4JWordTemplate();
			Map<String, Class<?>> dynamicElements = modelService
					.listModelElements(inputType, modelProperties);
			for (String key : dynamicElements.keySet()) {
				PlainTextContent plainTextContent = basicTemplate
						.addPlainTextContent();
				plainTextContent.setExpression(key);
				plainTextContent.setTitle(key);
				plainTextContent.setPlaceHolderContent(key);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			basicTemplate.writeAsOpenXML(out);
			return out.toByteArray();
		} catch (Exception e) {
			throw new WteException("Can not create BasicTemplate: "
					+ e.getMessage(), e);
		}
	}

	private void validateBeforeBuild() throws TemplateBuildException {
		for (String propertyName : modelService.listRequiredModelProperties()) {
			if (!modelProperties.containsKey(propertyName)) {
				throw new TemplateBuildException("required property missing ("
						+ propertyName + ")");
			}
		}
		if (StringUtils.isBlank(documentName) || StringUtils.isBlank(language)
				|| StringUtils.isBlank(user.getUserId())) {
			throw new TemplateBuildException(
					"documentName, language and author's userId must not be null or empty");
		}

	}

}