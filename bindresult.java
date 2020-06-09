/**
     * 校验控制层参数
     *
     * @param bindingResult
     * @return
     */
    public JsonResult validateControllerApiParams(BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            fieldErrorList = fieldErrorList.stream().sorted(Comparator.comparing(FieldError::getField)).collect(Collectors.toList());
            StringBuilder builder = new StringBuilder();
            fieldErrorList.forEach(error -> builder.append(error.getDefaultMessage()).append(Defs.LINE_SEPARATOR));
            log.error("--->>> {}", builder.toString());
            return JsonResult.fail(HttpStatus.BAD_REQUEST.value(), builder.toString());
        }

        return null;
    }
