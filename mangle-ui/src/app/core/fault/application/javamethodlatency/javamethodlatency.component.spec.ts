import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';
import { of, Observable } from 'rxjs';
import { JavaMethodLatencyComponent } from './javamethodlatency.component';
import { FaultService } from '../../fault.service';
import { EndpointService } from 'src/app/core/endpoint/endpoint.service';
import { Router } from '@angular/router';

describe('JavaMethodLatencyComponent', () => {
    let component: JavaMethodLatencyComponent;
    let faultService: FaultService;
    let endpointService: EndpointService;
    let fixture: ComponentFixture<JavaMethodLatencyComponent>;
    let router: Router;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                BrowserAnimationsModule,
                BrowserModule,
                FormsModule,
                HttpClientModule,
                CommonModule,
                ClarityModule,
                RouterTestingModule.withRoutes([{ path: 'java-method-latency', component: JavaMethodLatencyComponent }])
            ],
            declarations: [JavaMethodLatencyComponent],
            providers: [
                FaultService,
                EndpointService
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();
        fixture = TestBed.createComponent(JavaMethodLatencyComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        endpointService = TestBed.get(EndpointService);
        spyOn(endpointService, 'getAllEndpoints').and.returnValue(of([]));
        spyOn(endpointService, 'getDockerContainers').and.returnValue(of([]));
        faultService = TestBed.get(FaultService);
        spyOn(faultService, 'executeJavaMethodLatencyFault').and.returnValue(of({ "taskData": { "schedule": null } }));
        router = TestBed.get(Router);
        spyOn(router, 'navigateByUrl');
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should execute java-method-latency fault', () => {
        component.executeJavaMethodLatencyFault(component.faultFormData);
        expect(faultService.executeJavaMethodLatencyFault).toHaveBeenCalled();
    });

    it('should execute getDockerContainers', () => {
        component.getDockerContainers("DOCKER", '');
        expect(endpointService.getDockerContainers).toHaveBeenCalled();
    });

});
