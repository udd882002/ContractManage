package com.seven.contract.manage.service;

import com.seven.contract.manage.model.Label;

import java.util.List;
import java.util.Map;

public interface LabelService {

	public int addLabel(long mid, String labelName);

	public void deleteById(long id);

	public void updateLabel(Label label);

	public Label selectOneById(long id);

	public List<Label> selectList(Map<String, Object> params);
}
