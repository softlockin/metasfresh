/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2022 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package org.adempiere.ad.migration.validator.sql_migration_context_info;

import de.metas.reflist.ReferenceId;
import org.adempiere.ad.column.AdColumnId;
import org.adempiere.ad.migration.logger.MigrationScriptFileLoggerHolder;
import org.adempiere.ad.modelvalidator.annotations.Interceptor;
import org.adempiere.ad.modelvalidator.annotations.ModelChange;
import org.adempiere.ad.table.api.AdTableId;
import org.adempiere.ad.table.api.impl.TableIdsCache;
import org.compiere.model.I_AD_Ref_Table;
import org.compiere.model.ModelValidator;
import org.springframework.stereotype.Component;

@Interceptor(I_AD_Ref_Table.class)
@Component
public class AD_Ref_Table
{
	@ModelChange(timings = { ModelValidator.TYPE_BEFORE_NEW, ModelValidator.TYPE_BEFORE_CHANGE, ModelValidator.TYPE_BEFORE_DELETE })
	public void logSqlMigrationContextInfo(final I_AD_Ref_Table record)
	{
		if (MigrationScriptFileLoggerHolder.isDisabled())
		{
			return;
		}

		final ReferenceId referenceId = ReferenceId.ofRepoId(record.getAD_Reference_ID());
		MigrationScriptFileLoggerHolder.logComment("Reference: " + AD_Ref_List.retrieveADReferenceName(referenceId));

		final AdTableId adTableId = AdTableId.ofRepoId(record.getAD_Table_ID());
		final String tableName = TableIdsCache.instance.getTableNameIfPresent(adTableId).orElseGet(() -> "<" + adTableId + ">");
		MigrationScriptFileLoggerHolder.logComment("Table: " + tableName);

		final AdColumnId keyColumnId = AdColumnId.ofRepoId(record.getAD_Key());
		final String keyColumnName = AD_Field.retrieveColumnNameFQ(keyColumnId);
		MigrationScriptFileLoggerHolder.logComment("Key: " + keyColumnName);
	}
}
