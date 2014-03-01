//
//  SummaryListViewController.h
//  OurFarm
//  SS DD
//  Created by tian hao on 13-6-7.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MNMBottomPullToRefreshManager.h"
#import <CoreLocation/CoreLocation.h>

@interface SummaryListViewController : UIViewController <UITableViewDelegate, UITableViewDataSource, UIScrollViewDelegate, MNMBottomPullToRefreshManagerClient, CLLocationManagerDelegate> {
@private
    /**
     * Table
     */
    UITableView *table_;
    
    /**
     * Pull to refresh manager
     */
    MNMBottomPullToRefreshManager *pullToRefreshManager_;
    
    /**
     * Reloads (for testing purposes)
     */
    NSUInteger reloads_;
    /**
     * 定位管理类
     */
    CLLocationManager *locationManager;
    CLLocationCoordinate2D coordinate;
}

/**
 * Provides readwrite access to the table_. Exported to IB
 */
@property (nonatomic, readwrite, strong) IBOutlet UITableView *table;

@property (strong) NSString *resJson;
@property (strong) NSNumber *nearbyType;
@end
