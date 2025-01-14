/*
 * #%L
 * de.metas.business
 * %%
 * Copyright (C) 2021 metas GmbH
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

package de.metas.project.service;

import com.google.common.collect.ImmutableList;
import de.metas.logging.LogManager;
import de.metas.order.OrderAndLineId;
import de.metas.order.OrderId;
import de.metas.project.ProjectAndLineId;
import de.metas.project.ProjectData;
import de.metas.project.ProjectId;
import de.metas.project.ProjectLine;
import de.metas.project.ProjectTypeId;
import de.metas.project.ProjectTypeRepository;
import de.metas.project.service.listeners.CompositeProjectStatusListener;
import de.metas.project.service.listeners.ProjectStatusListener;
import de.metas.servicerepair.project.CreateServiceOrRepairProjectRequest;
import lombok.NonNull;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.InterfaceWrapperHelper;
import org.compiere.model.I_C_Project;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService
{
	private static final Logger logger = LogManager.getLogger(ProjectService.class);
	private final ProjectTypeRepository projectTypeRepository;
	private final ProjectRepository projectRepository;
	private final ProjectLineRepository projectLineRepository;
	private final CompositeProjectStatusListener projectStatusListeners;

	public ProjectService(
			@NonNull final ProjectTypeRepository projectTypeRepository,
			@NonNull final ProjectRepository projectRepository,
			@NonNull final ProjectLineRepository projectLineRepository,
			@NonNull final Optional<List<ProjectStatusListener>> projectStatusListeners)
	{
		this.projectTypeRepository = projectTypeRepository;
		this.projectRepository = projectRepository;
		this.projectLineRepository = projectLineRepository;
		this.projectStatusListeners = CompositeProjectStatusListener.ofList(projectStatusListeners.orElseGet(ImmutableList::of));
		logger.info("projectClosedListeners: {}", projectStatusListeners);
	}

	public I_C_Project getRecordById(@NonNull final ProjectId id)
	{
		return projectRepository.getRecordById(id);
	}

	public List<ProjectLine> getLines(@NonNull final ProjectId projectId)
	{
		return projectLineRepository.retrieveLines(projectId);
	}

	public ProjectLine getLineById(@NonNull final ProjectAndLineId projectLineId)
	{
		return projectLineRepository.retrieveLineById(projectLineId);
	}

	public List<ProjectLine> getLinesByOrderId(@NonNull final OrderId orderId)
	{
		return projectLineRepository.retrieveLinesByOrderId(orderId);
	}

	public ProjectId createProject(@NonNull final CreateServiceOrRepairProjectRequest request)
	{
		final ProjectData.ProjectDataBuilder projectDataBuilder = ProjectData.builder()
				.orgId(request.getOrgId())
				.bPartnerId(request.getBpartnerAndLocationId().getBpartnerId())
				.bPartnerLocationId(request.getBpartnerAndLocationId())
				.contactId(request.getContactId())
				.currencyId(request.getCurrencyId())
				.priceListVersionId(request.getPriceListVersionId())
				.warehouseId(request.getWarehouseId());

		//
		// Project Type (and related)
		final ProjectTypeId projectTypeId = projectTypeRepository.getFirstIdByProjectCategoryAndOrg(
				request.getProjectCategory(),
				request.getOrgId());

		projectDataBuilder.projectTypeId(projectTypeId);

		final ProjectId projectId = projectRepository.create(projectDataBuilder.build()).getId();

		for (final CreateServiceOrRepairProjectRequest.ProjectLine lineRequest : request.getLines())
		{
			projectLineRepository.createProjectLine(lineRequest, projectId, request.getOrgId());
		}

		return projectId;
	}

	public void createProjectLine(@NonNull final CreateProjectLineRequest request)
	{
		projectLineRepository.createProjectLine(request);
	}

	public ProjectLine changeProjectLine(@NonNull final ChangeProjectLineRequest request)
	{
		return projectLineRepository.changeProjectLine(
				request.getProjectLineId(),
				projectLine -> changeProjectLine(request, projectLine));

	}

	private void changeProjectLine(final ChangeProjectLineRequest request, final ProjectLine projectLine)
	{
		if (request.getCommittedQtyToAdd() != null)
		{
			projectLine.addCommittedQty(request.getCommittedQtyToAdd());
		}
	}

	public void linkToOrderLine(
			@NonNull final ProjectAndLineId projectLineId,
			@NonNull final OrderAndLineId orderLineId)
	{
		projectLineRepository.linkToOrderLine(projectLineId, orderLineId);
	}

	public boolean isClosed(@NonNull final ProjectId projectId)
	{
		final I_C_Project projectRecord = getRecordById(projectId);
		return projectRecord.isProcessed();
	}

	public void closeProject(@NonNull final ProjectId projectId)
	{
		final I_C_Project projectRecord = getRecordById(projectId);
		if (projectRecord.isProcessed())
		{
			throw new AdempiereException("Project already closed: " + projectId);
		}

		projectStatusListeners.onBeforeClose(projectRecord);

		projectLineRepository.markLinesAsProcessed(projectId);

		projectRecord.setProcessed(true);
		InterfaceWrapperHelper.saveRecord(projectRecord);

		projectStatusListeners.onAfterClose(projectRecord);
	}

	public void uncloseProject(@NonNull final ProjectId projectId)
	{
		final I_C_Project projectRecord = getRecordById(projectId);
		if (!projectRecord.isProcessed())
		{
			throw new AdempiereException("Project not closed: " + projectId);
		}

		projectStatusListeners.onBeforeUnClose(projectRecord);

		projectLineRepository.markLinesAsNotProcessed(projectId);

		projectRecord.setProcessed(false);
		InterfaceWrapperHelper.saveRecord(projectRecord);

		projectStatusListeners.onAfterUnClose(projectRecord);
	}

}
