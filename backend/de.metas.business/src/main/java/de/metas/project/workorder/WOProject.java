/*
 * #%L
 * de.metas.business
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

package de.metas.project.workorder;

import com.google.common.collect.ImmutableSet;
import de.metas.project.ProjectId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Work Order Project
 */
@Value
@Builder
public class WOProject
{
	@NonNull ProjectId projectId;
	@NonNull String name;
	@Nullable ProjectId parentProjectId;

	public static Set<ProjectId> extractProjectIds(final List<WOProject> projects)
	{
		return projects.stream().map(WOProject::getProjectId).collect(ImmutableSet.toImmutableSet());
	}
}
