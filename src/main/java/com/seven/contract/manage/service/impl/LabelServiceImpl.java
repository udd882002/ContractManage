package com.seven.contract.manage.service.impl;

import com.seven.contract.manage.dao.LabelDao;
import com.seven.contract.manage.model.Label;
import com.seven.contract.manage.service.LabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class LabelServiceImpl implements LabelService {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LabelDao labelDao;

	public int addLabel(long mid, String labelName) {

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("labelName", labelName);
		List<Label> labels = labelDao.selectList(params);
		if (labels.size() > 0) {
			return labels.get(0).getId();
		} else {
			Label label = new Label();
			label.setMid(mid);
			label.setLabelName(labelName);
			label.setAddTime(new Date());
			labelDao.insert(label);

			return label.getId();
		}


	}

	public void updateLabel(Label label){
		labelDao.update(label);
	}
	public void deleteById(long id) {
		labelDao.deleteById(id);
	}

	public Label selectOneById(long id) {
		Label label = labelDao.selectOne(id);
		return label;
	}

	@Override
	public List<Label> selectList(Map<String, Object> params) {
		return labelDao.selectList(params);
	}

}
