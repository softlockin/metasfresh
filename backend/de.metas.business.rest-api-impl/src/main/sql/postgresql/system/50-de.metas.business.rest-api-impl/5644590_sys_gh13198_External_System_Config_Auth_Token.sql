-- Column: ExternalSystem_Config.AuthToken
-- 2022-06-21T14:16:49.323Z
INSERT INTO AD_Column (AD_Client_ID,AD_Column_ID,AD_Element_ID,AD_Org_ID,AD_Reference_ID,AD_Table_ID,ColumnName,Created,CreatedBy,DDL_NoForeignKey,EntityType,FacetFilterSeqNo,FieldLength,IsActive,IsAdvancedText,IsAllowLogging,IsAlwaysUpdateable,IsAutoApplyValidationRule,IsAutocomplete,IsCalculated,IsDimension,IsDLMPartitionBoundary,IsEncrypted,IsExcludeFromZoomTargets,IsFacetFilter,IsForceIncludeInGeneratedModel,IsGenericZoomKeyColumn,IsGenericZoomOrigin,IsIdentifier,IsKey,IsLazyLoading,IsMandatory,IsParent,IsSelectionColumn,IsShowFilterIncrementButtons,IsShowFilterInline,IsStaleable,IsSyncDatabase,IsTranslated,IsUpdateable,IsUseDocSequence,MaxFacetsToFetch,Name,SelectionColumnSeqNo,SeqNo,Updated,UpdatedBy,Version) VALUES (0,583467,543920,0,10,541576,'AuthToken',TO_TIMESTAMP('2022-06-21 17:16:49','YYYY-MM-DD HH24:MI:SS'),100,'N','de.metas.externalsystem',0,100,'Y','N','Y','N','N','N','N','N','N','N','Y','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','Y','N',0,'Authentifizierungs-Token',0,0,TO_TIMESTAMP('2022-06-21 17:16:49','YYYY-MM-DD HH24:MI:SS'),100,0)
;

-- 2022-06-21T14:16:49.326Z
INSERT INTO AD_Column_Trl (AD_Language,AD_Column_ID, Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy,IsActive) SELECT l.AD_Language, t.AD_Column_ID, t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy,'Y' FROM AD_Language l, AD_Column t WHERE l.IsActive='Y'AND (l.IsSystemLanguage='Y') AND t.AD_Column_ID=583467 AND NOT EXISTS (SELECT 1 FROM AD_Column_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Column_ID=t.AD_Column_ID)
;

-- 2022-06-21T14:16:49.358Z
/* DDL */  select update_Column_Translation_From_AD_Element(543920) 
;

-- Field: External System Config -> External System Config -> Authentifizierungs-Token
-- Column: ExternalSystem_Config.AuthToken
-- 2022-06-21T14:19:09.206Z
INSERT INTO AD_Field (AD_Client_ID,AD_Column_ID,AD_Field_ID,AD_Org_ID,AD_Tab_ID,ColumnDisplayLength,Created,CreatedBy,DisplayLength,EntityType,IncludedTabHeight,IsActive,IsDisplayed,IsDisplayedGrid,IsEncrypted,IsFieldOnly,IsHeading,IsReadOnly,IsSameLine,Name,SeqNo,SeqNoGrid,SortNo,SpanX,SpanY,Updated,UpdatedBy) VALUES (0,583467,700732,0,543321,0,TO_TIMESTAMP('2022-06-21 17:19:09','YYYY-MM-DD HH24:MI:SS'),100,0,'de.metas.externalsystem',0,'Y','Y','Y','N','N','N','N','N','Authentifizierungs-Token',0,20,0,1,1,TO_TIMESTAMP('2022-06-21 17:19:09','YYYY-MM-DD HH24:MI:SS'),100)
;

-- 2022-06-21T14:19:09.206Z
INSERT INTO AD_Field_Trl (AD_Language,AD_Field_ID, Description,Help,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy,IsActive) SELECT l.AD_Language, t.AD_Field_ID, t.Description,t.Help,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy,'Y' FROM AD_Language l, AD_Field t WHERE l.IsActive='Y'AND (l.IsSystemLanguage='Y') AND t.AD_Field_ID=700732 AND NOT EXISTS (SELECT 1 FROM AD_Field_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Field_ID=t.AD_Field_ID)
;

-- 2022-06-21T14:19:09.222Z
/* DDL */  select update_FieldTranslation_From_AD_Name_Element(543920) 
;

-- 2022-06-21T14:19:09.237Z
DELETE FROM AD_Element_Link WHERE AD_Field_ID=700732
;

-- 2022-06-21T14:19:09.237Z
/* DDL */ select AD_Element_Link_Create_Missing_Field(700732)
;

-- 2022-06-21T14:21:33.148Z
INSERT INTO AD_UI_ElementGroup (AD_Client_ID,AD_Org_ID,AD_UI_Column_ID,AD_UI_ElementGroup_ID,Created,CreatedBy,IsActive,Name,SeqNo,Updated,UpdatedBy) VALUES (0,0,543205,549378,TO_TIMESTAMP('2022-06-21 17:21:33','YYYY-MM-DD HH24:MI:SS'),100,'Y','auth',30,TO_TIMESTAMP('2022-06-21 17:21:33','YYYY-MM-DD HH24:MI:SS'),100)
;

-- UI Element: External System Config -> External System Config.Authentifizierungs-Token
-- Column: ExternalSystem_Config.AuthToken
-- 2022-06-21T14:22:14.963Z
INSERT INTO AD_UI_Element (AD_Client_ID,AD_Field_ID,AD_Org_ID,AD_Tab_ID,AD_UI_Element_ID,AD_UI_ElementGroup_ID,AD_UI_ElementType,Created,CreatedBy,IsActive,IsAdvancedField,IsAllowFiltering,IsDisplayed,IsDisplayed_SideList,IsDisplayedGrid,IsMultiLine,MultiLine_LinesCount,Name,SeqNo,SeqNo_SideList,SeqNoGrid,Updated,UpdatedBy) VALUES (0,700732,0,543321,609614,549378,'F',TO_TIMESTAMP('2022-06-21 17:22:14','YYYY-MM-DD HH24:MI:SS'),100,'Y','N','N','Y','N','N','N',0,'Authentifizierungs-Token',10,0,0,TO_TIMESTAMP('2022-06-21 17:22:14','YYYY-MM-DD HH24:MI:SS'),100)
;

/*
 * #%L
 * de.metas.business.rest-api-impl
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

-- 2022-06-21T14:22:33.373Z
/* DDL */ SELECT public.db_alter_table('ExternalSystem_Config','ALTER TABLE public.ExternalSystem_Config ADD COLUMN AuthToken VARCHAR(100)')
;

-- 2022-06-21T16:25:52.366Z
INSERT INTO t_alter_column values('externalsystem_config','AuthToken','VARCHAR(100)',null,null)
;

