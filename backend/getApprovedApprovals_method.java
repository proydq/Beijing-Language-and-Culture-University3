@Override
    public ResponsePageDataEntity<ApprovalListResponse> getApprovedApprovals(ApprovalListRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String currentUserId = SecurityUtil.getCurrentUserId();
        
        // 查询当前用户已通过的审批记录
        List<BookingApproval> approvedApprovals = bookingApprovalRepository
            .findByApproverIdAndCstmIdAndApprovalActionOrderByApprovalTimeDesc(
                currentUserId, customerId, BookingApproval.ApprovalAction.APPROVE);
        
        // 获取所有相关的预约ID
        List<String> bookingIds = approvedApprovals.stream()
            .map(BookingApproval::getBookingId)
            .distinct()
            .collect(Collectors.toList());
        
        if (bookingIds.isEmpty()) {
            ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
            result.setRows(new ArrayList<>());
            result.setTotal(0L);
            return result;
        }
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 查询对应的预约记录
        Specification<RoomBooking> bookingSpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 预约ID过滤（只查询当前用户审批过的预约）
            predicates.add(root.get("id").in(bookingIds));
            
            // 预约名称模糊搜索
            if (StringUtils.hasText(request.getReservationName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), "%" + request.getReservationName() + "%"));
            }
            
            // 申请人姓名模糊搜索
            if (StringUtils.hasText(request.getApplicantName())) {
                predicates.add(criteriaBuilder.like(root.get("applicantName"), "%" + request.getApplicantName() + "%"));
            }
            
            // 日期范围过滤
            if (StringUtils.hasText(request.getStartDate())) {
                try {
                    LocalDateTime startDateTime = LocalDate.parse(request.getStartDate()).atTime(0, 0, 0);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", request.getStartDate());
                }
            }
            
            if (StringUtils.hasText(request.getEndDate())) {
                try {
                    LocalDateTime endDateTime = LocalDate.parse(request.getEndDate()).atTime(23, 59, 59);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endDateTime));
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", request.getEndDate());
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行分页查询
        Page<RoomBooking> page = roomBookingRepository.findAll(bookingSpec, pageable);
        
        // 转换为响应DTO
        List<ApprovalListResponse> responseList = page.getContent().stream()
            .map(this::convertToApprovalListResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal(page.getTotalElements());
        return result;
    }