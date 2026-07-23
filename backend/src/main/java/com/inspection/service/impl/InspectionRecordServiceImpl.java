package com.inspection.service.impl;

import com.inspection.dto.InspectionReportDTO;
import com.inspection.entity.InspectionImage;
import com.inspection.entity.InspectionRecord;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.InspectionImageMapper;
import com.inspection.mapper.InspectionRecordMapper;
import com.inspection.service.InspectionRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 巡检记录服务实现
 */
@Service
public class InspectionRecordServiceImpl implements InspectionRecordService {

    private final InspectionRecordMapper recordMapper;
    private final InspectionImageMapper imageMapper;

    /**
     * 构造器注入
     */
    public InspectionRecordServiceImpl(InspectionRecordMapper recordMapper,
                                       InspectionImageMapper imageMapper) {
        this.recordMapper = recordMapper;
        this.imageMapper = imageMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitReport(InspectionReportDTO dto) {
        // 1. 参数校验
        validate(dto);

        // 2. DTO 转 Entity
        InspectionRecord record = toEntity(dto);

        // 3. 插入巡检记录
        recordMapper.insert(record);
        Long recordId = record.getId();

        // 4. 插入图片（如果有）
        List<String> imageUrls = dto.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String url : imageUrls) {
                if (url != null && !url.isBlank()) {
                    InspectionImage image = new InspectionImage();
                    image.setRecordId(recordId);
                    image.setImageUrl(url);
                    imageMapper.insert(image);
                }
            }
        }

        return recordId;
    }

    @Override
    public InspectionRecord getById(Long id) {
        InspectionRecord record = recordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(404, "巡检记录不存在: id=" + id);
        }
        return record;
    }

    /**
     * 参数校验
     */
    private void validate(InspectionReportDTO dto) {
        if (dto.getDepartment() == null || dto.getDepartment().isBlank()) {
            throw new BusinessException("部门不能为空");
        }
        if (dto.getEmployeeName() == null || dto.getEmployeeName().isBlank()) {
            throw new BusinessException("姓名不能为空");
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank()) {
            throw new BusinessException("手机号不能为空");
        }
        if (dto.getArea() == null || dto.getArea().isBlank()) {
            throw new BusinessException("区域不能为空");
        }
        if (dto.getInspectionTime() == null) {
            throw new BusinessException("巡检时间不能为空");
        }
        if (dto.getDeviceName() == null || dto.getDeviceName().isBlank()) {
            throw new BusinessException("设备名称不能为空");
        }
        if (dto.getResult() == null) {
            throw new BusinessException("巡检结果不能为空");
        }
        if (dto.getResult() < 0 || dto.getResult() > 2) {
            throw new BusinessException("巡检结果值无效，允许值：0=正常, 1=故障, 2=隐患");
        }
    }

    /**
     * DTO 转 Entity
     */
    private InspectionRecord toEntity(InspectionReportDTO dto) {
        InspectionRecord record = new InspectionRecord();
        record.setDepartment(dto.getDepartment());
        record.setEmployeeName(dto.getEmployeeName());
        record.setPhoneNumber(dto.getPhoneNumber());
        record.setArea(dto.getArea());
        record.setInspectionTime(dto.getInspectionTime());
        record.setDeviceName(dto.getDeviceName());
        record.setResult(dto.getResult());
        record.setDescription(dto.getDescription());
        return record;
    }
}