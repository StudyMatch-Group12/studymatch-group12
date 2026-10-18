import React from 'react';
import {
    PolarAngleAxis,
    PolarGrid,
    PolarRadiusAxis,
    Radar,
    RadarChart,
    ResponsiveContainer,
    Tooltip,
} from 'recharts';
import { MAX_LEVEL, MIN_LEVEL, MIN_RADAR_AXES } from '../utils/chartData';

// data: output of formatCompetenciesForRadar()
export default function CompetencyRadarChart({ data }) {
    if (!data || data.length === 0) {
        return <p className="empty-state">This student has no competency data yet.</p>;
    }

    // With fewer than three axes a radar has no area, so show simple bars instead
    if (data.length < MIN_RADAR_AXES) {
        return (
            <ul className="competency-bars">
                {data.map((item) => (
                    <li key={item.competency}>
                        <span className="competency-bar-label">{item.competency}</span>
                        <span className="competency-bar-track">
                            <span
                                className="competency-bar-fill"
                                style={{ width: `${(item.level / MAX_LEVEL) * 100}%` }}
                            />
                        </span>
                        <span className="competency-bar-value">{item.level}/{MAX_LEVEL}</span>
                    </li>
                ))}
            </ul>
        );
    }

    return (
        <div className="radar-chart" data-testid="competency-radar-chart">
            <ResponsiveContainer width="100%" height={360}>
                <RadarChart data={data} outerRadius="75%">
                    <PolarGrid />
                    <PolarAngleAxis dataKey="competency" />
                    <PolarRadiusAxis angle={90} domain={[MIN_LEVEL, MAX_LEVEL]} tickCount={MAX_LEVEL + 1} />
                    <Radar
                        name="Proficiency level"
                        dataKey="level"
                        stroke="#4f46e5"
                        fill="#4f46e5"
                        fillOpacity={0.35}
                    />
                    <Tooltip formatter={(value) => [`${value} / ${MAX_LEVEL}`, 'Level']} />
                </RadarChart>
            </ResponsiveContainer>
        </div>
    );
}
