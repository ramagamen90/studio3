/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.aptana.ui.preferences.AptanaPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public abstract class CommonEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * Button for enabling occurrence highlighting
	 */
	private Composite appearanceComposite;
	private Composite advancedOptions;
	private BooleanFieldEditor markOccurences;

	/**
	 * EditorsPreferencePage
	 */
	public CommonEditorPreferencePage()
	{
		super(GRID);
		setDescription(Messages.CommonEditorPreferencePage_Editor_Preferences);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		appearanceComposite = getFieldEditorParent();
		Composite group = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.EditorsPreferencePage_Formatting);

		markOccurences = new BooleanFieldEditor(IPreferenceConstants.EDITOR_MARK_OCCURRENCES,
				Messages.EditorsPreferencePage_MarkOccurrences, group);
		addField(markOccurences);

		// Perhaps need expand/collapse arrows
		advancedOptions = createAdvancedOccurrenceSection(group);

		// Link to general text annotation prefs from Eclipse
		Link link = new Link(group, SWT.NONE);
		link.setText(Messages.CommonEditorPreferencePage_Default_Editor_Preference_Link);
		link.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), e.text, null, null);
			}
		});
	}

	/**
	 * Creates the advanced group of occurrence options. If there are no options, we just delete the group.
	 * 
	 * @param parent
	 * @return
	 */
	private Composite createAdvancedOccurrenceSection(Composite parent)
	{
		Composite aOptions = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		aOptions.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalIndent = 18;
		data.horizontalSpan = 2;
		aOptions.setLayoutData(data);

		createMarkOccurrenceOptions(aOptions);

		aOptions.setVisible(false);

		// Perhaps better way?
		if (aOptions.getChildren().length == 0)
		{
			aOptions.dispose();
			return null;
		}
		else
			return aOptions;
	}

	/**
	 * Create any extra "Mark Occurrence" options if necessary.
	 * 
	 * @param parent
	 */
	protected abstract void createMarkOccurrenceOptions(Composite parent);

	/**
	 * Listens for changes in showing/hiding advanced options
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource() == markOccurences && advancedOptions != null)
		{
			if (!(Boolean) event.getNewValue())
			{
				hideAdvancedOccurrenceSection();
			}
			else
			{
				showAdvancedOccurrenceSection();
			}
		}
	}

	private void hideAdvancedOccurrenceSection()
	{
		advancedOptions.setVisible(false);
		if (advancedOptions.getLayoutData() != null)
		{
			((GridData) advancedOptions.getLayoutData()).exclude = true;
		}
		appearanceComposite.layout(true, true);
	}

	private void showAdvancedOccurrenceSection()
	{
		advancedOptions.setVisible(true);
		if (advancedOptions.getLayoutData() != null)
		{
			((GridData) advancedOptions.getLayoutData()).exclude = false;
		}
		appearanceComposite.layout(true, true);
	}

	public void init(IWorkbench workbench)
	{
	}

	protected void initialize()
	{
		super.initialize();

		if (advancedOptions != null)
		{
			if (getPreferenceStore().getBoolean(IPreferenceConstants.EDITOR_MARK_OCCURRENCES))
			{
				showAdvancedOccurrenceSection();
			}
			else
			{
				hideAdvancedOccurrenceSection();
			}
		}
	}
}
