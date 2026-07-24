package com.inspection.service.impl;

import com.inspection.common.PageResult;
import com.inspection.dto.InspectionQueryDTO;
import com.inspection.dto.InspectionReportDTO;
import com.inspection.entity.InspectionImage;
import com.inspection.entity.InspectionRecord;
import com.inspection.exception.BusinessException;
import com.inspection.mapper.InspectionImageMapper;
import com.inspection.mapper.InspectionRecordMapper;
import com.inspection.common.UserContext;
import com.inspection.service.InspectionRecordService;
import com.inspection.vo.InspectionRecordVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 巡检记录服务实现
 */
@Service
public class InspectionRecordServiceImpl implements InspectionRecordService {

    private final InspectionRecordMapper recordMapper;
    private final InspectionImageMapper imageMapper;

    @Value("${file.base-url}")
    private String fileBaseUrl;

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

        // 3. 从认证上下文获取当前用户
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未认证");
        }
        record.setUserId(userId);

        // 4. 插入巡检记录
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

    @Override
    public InspectionRecordVO getByIdVO(Long id) {
        InspectionRecord record = recordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(404, "巡检记录不存在: id=" + id);
        }
        return toVO(record);
    }

    @Override
    public PageResult<InspectionRecordVO> queryList(InspectionQueryDTO dto) {
        // 权限过滤：非 ADMIN 角色只查自己的数据
        if (UserContext.getUserId() != null && !UserContext.isAdmin()) {
            dto.setUserId(UserContext.getUserId());
        }

        // 参数校验
        if (dto.getPageNum() == null || dto.getPageNum() < 1) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null || dto.getPageSize() < 1) {
            dto.setPageSize(10);
        }
        if (dto.getPageSize() > 100) {
            dto.setPageSize(100);
        }

        // 查询数据
        List<InspectionRecord> records = recordMapper.selectByCondition(dto);
        long total = recordMapper.count(dto);

        // Entity → VO（逐条查询关联图片）
        List<InspectionRecordVO> voList = records.stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, total, dto.getPageNum(), dto.getPageSize());
    }

    /**
     * Entity → VO 转换
     */
    private InspectionRecordVO toVO(InspectionRecord record) {
        InspectionRecordVO vo = new InspectionRecordVO();
        vo.setId(record.getId());
        vo.setDepartment(record.getDepartment());
        vo.setEmployeeName(record.getEmployeeName());
        vo.setPhoneNumber(record.getPhoneNumber());
        vo.setArea(record.getArea());
        vo.setInspectionTime(record.getInspectionTime());
        vo.setDeviceName(record.getDeviceName());
        vo.setResult(record.getResult());
        vo.setDescription(record.getDescription());
        vo.setCreatedAt(record.getCreatedAt());

        // 查询关联图片，拼接完整访问 URL（数据库存相对路径 /uploads/xxx，返回完整 HTTP URL）
        List<InspectionImage> images = imageMapper.selectByRecordId(record.getId());
        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = images.stream()
                    .map(img -> fileBaseUrl + img.getImageUrl())
                    .collect(Collectors.toList());
            vo.setImageUrls(imageUrls);
        } else {
            vo.setImageUrls(new ArrayList<>());
        }

        return vo;
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