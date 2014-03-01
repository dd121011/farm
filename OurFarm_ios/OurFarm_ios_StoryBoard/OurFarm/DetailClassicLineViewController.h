//
//  DeatilClassicLineViewController.h
//  OurFarm
//
//  Created by tian hao on 13-6-20.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
@class ClassicLine;

@interface DetailClassicLineViewController : UITableViewController

//对象数组
@property ClassicLine *classicLine;
@property NSMutableArray *lines;

@property (strong, nonatomic) IBOutlet UILabel *name;
@property (strong, nonatomic) IBOutlet UILabel *itinerarySummary;
@property (strong, nonatomic) IBOutlet UIImageView *picMap;




@end
