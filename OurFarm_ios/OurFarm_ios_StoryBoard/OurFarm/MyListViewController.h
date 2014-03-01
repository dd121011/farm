//
//  MyListViewController.h
//  OurFarm
//
//  Created by 李 凤勇 on 13-6-26.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MyListViewController : UITableViewController {
    
}

@property (nonatomic, readwrite, strong) IBOutlet UITableView *table;

@property (assign) NSNumber *myType;


@end
