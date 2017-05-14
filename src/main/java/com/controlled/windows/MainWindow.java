package com.controlled.windows;


import com.controlled.Model.ComputerModel;


import com.controlled.Server.ScreenModule;
import com.controlled.Server.BusinessModule;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainWindow extends Application {
	
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("远程桌面控制");
		stage.setScene(createScene());
		stage.setResizable(false);
		stage.setOnCloseRequest(e->{
			ScreenModule.getInstance().closeSession();
			BusinessModule.getInstance().closeSession();
			System.exit(0);
		});
		new ScreenModule();
		stage.show();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(args);
	}
	
	public Scene createScene(){
		TabPane tabPane = new TabPane();
		tabPane.setSide(Side.LEFT);
		tabPane.setRotateGraphic(true);
		
		Tab tab1 = new Tab();
		tab1.setClosable(false);
		Label label1 = new Label("状态");
		label1.setRotate(90);
		StackPane stp = new StackPane(new Group(label1));
		stp.setRotate(90);
		tab1.setGraphic(stp);
		Label nameLabel = new Label("用户名:");
		Label name = new Label(ComputerModel.getInstance().getUsername());
		Label stat = new Label("已成功连接服务器");
		stat.setFont(new Font(20));
		GridPane gridpane = new GridPane();
		gridpane.add(nameLabel, 0, 0);
		gridpane.add(name, 1, 0);
		gridpane.add(stat, 0, 1);
		tab1.setContent(gridpane);
//
//		Tab tab2 = new Tab();
//		tab2.setClosable(false);
//		Label label2 = new Label("设置");
//		label2.setRotate(90);
//		stp = new StackPane(new Group(label2));
//		stp.setRotate(90);
//		tab2.setGraphic(stp);
//		Button logout = new Button("注销");
//		logout.setOnAction(e->{
//			//注销
//			//TODO
//		});
//		Button changepwd = new Button("更改密码");
//		changepwd.setOnAction(e->{
//			//更改密码
//			//TODO
//		});
//		CheckBox checkBox = new CheckBox("开机启动");
//		checkBox.setSelected(false);
//		GridPane gridpane1 = new GridPane();
//		gridpane1.add(checkBox, 0, 0);
//		gridpane1.add(changepwd, 0, 1);
//		gridpane1.add(logout, 1, 1);
//		tab2.setContent(gridpane1);
		
		
		tabPane.getTabs().addAll(tab1);
		tabPane.setTabMinHeight(100);
		tabPane.setTabMaxHeight(100);

		Scene scene = new Scene(tabPane, 350, 250);
		return scene;
		
	}

}
