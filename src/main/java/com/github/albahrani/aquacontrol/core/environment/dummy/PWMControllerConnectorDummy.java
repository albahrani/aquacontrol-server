/**
 * Copyright © 2017 albahrani (https://github.com/albahrani)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.albahrani.aquacontrol.core.environment.dummy;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;

public class PWMControllerConnectorDummy implements PWMControllerConnector {

	LightFrame lf = new LightFrame();

	private Map<Pin, Integer> currentValues = new HashMap<>();

	public PWMControllerConnectorDummy() {
		SwingUtilities.invokeLater(() -> {
				lf.setSize(400, 400);
				lf.setVisible(true);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.albahrani.aquacontrol.core.environment.PWMControllerConnector#
	 * setPwmValue(java.util.Set, int)
	 */
	@Override
	public void setPwmValue(Set<Pin> pinList, int duration) {
		pinList.forEach(pin -> setPWmValueSinglePin(pin, duration));
	}

	private void setPWmValueSinglePin(Pin pin, int duration) {
		currentValues.put(pin, duration);

		SwingUtilities.invokeLater(() -> {
				lf.setValue(pin, duration);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.albahrani.aquacontrol.lightdaemon.environment.PWMControllerConnector#
	 * shutdown()
	 */
	@Override
	public void shutdown() {
		SwingUtilities.invokeLater(() -> {
				lf.setVisible(false);
				lf.dispose();
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.albahrani.aquacontrol.lightdaemon.environment.PWMControllerConnector#
	 * provisionPwmOutputPin(com.pi4j.io.gpio.Pin)
	 */
	@Override
	public void provisionPwmOutputPin(Pin pin) {
		SwingUtilities.invokeLater(() -> {
				lf.setValue(pin, null);
		});
	}

	static class LightFrame extends JFrame {

		private static final long serialVersionUID = 1L;
		private LightFrameModel model = new LightFrameModel();

		public LightFrame() {
			this.setTitle("LightFrame");
			this.setLayout(new BorderLayout());

			JTable table = new JTable(this.model);
			JScrollPane scrollpane = new JScrollPane(table);

			this.add(scrollpane, BorderLayout.CENTER);
		}

		public void setValue(Pin pin, Integer duration) {
			this.model.setValue(pin, duration);

		}
	}

	static class LightFrameModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private ArrayList<Pin> channels = new ArrayList<>();
		private HashMap<Pin, Integer> pwmDuration = new HashMap<>();
		private HashMap<Pin, Float> pwmPercentage = new HashMap<>();

		@Override
		public int getRowCount() {
			return this.channels.size();
		}

		public void setValue(Pin pin, Integer duration) {
			int rowIndex = this.channels.indexOf(pin);

			this.pwmDuration.put(pin, duration);
			this.pwmPercentage.put(pin, LightFrameModel.calculatePercentage(duration));

			if (rowIndex < 0) {
				this.channels.add(pin);
				rowIndex = this.channels.size() - 1;
				fireTableRowsInserted(rowIndex, rowIndex);
			} else {
				fireTableRowsUpdated(rowIndex, rowIndex);
			}
		}

		private static Float calculatePercentage(Integer duration) {
			if (duration == null) {
				return null;
			}
			return (duration / (PCA9685GpioProvider.PWM_STEPS - 1.0f)) * 100.0f;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int column) {
			String columnName = null;

			switch (column) {
			case 0:
				columnName = "Pin";
				break;
			case 1:
				columnName = "PWM (%)";
				break;

			case 2:
				columnName = "PWM (ms)";
				break;
			default:
				break;
			}

			return columnName;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			Object retVal = null;

			Pin channel = this.channels.get(rowIndex);

			switch (columnIndex) {
			case 0:
				retVal = channel.getName();
				break;

			case 1:
				Float pwmPerc = this.pwmPercentage.get(channel);
				if (pwmPerc != null) {
					retVal = String.format("%3.2f", pwmPerc);
				}
				break;

			case 2:
				retVal = this.pwmDuration.get(channel);
				break;

			default:
				break;
			}

			return retVal;
		}

	}
}
